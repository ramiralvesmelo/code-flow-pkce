package br.com.ramiralvesmelo.appclient.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.DispatcherType;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain security(HttpSecurity http, ClientRegistrationRepository clients) throws Exception {
        var oidcLogout = new OidcClientInitiatedLogoutSuccessHandler(clients);
        // para onde voltar depois do logout no Keycloak
        oidcLogout.setPostLogoutRedirectUri("{baseUrl}/");

        http
            .authorizeHttpRequests(auth -> auth
                // Permite forwards internos (ex.: /WEB-INF/jsp/*.jsp)
                .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()

                // Recursos estáticos comuns (css, js, images, webjars…)
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                // Suas rotas públicas
                .requestMatchers(
                        "/",
                        "/index",
                        "/index.html",
                        "/public/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/webjars/**",
                        "/actuator/health",
                        "/error",
                        "/login",
                        "/login/**",
                        "/oauth2/**",
                        "/login/oauth2/**"
                ).permitAll()

                // Demais endpoints requerem autenticação
                .anyRequest().authenticated()
            )

            // Não força sempre redirecionar para "/" após login
            .oauth2Login(oauth -> oauth.defaultSuccessUrl("/", false))

            .oauth2Client(Customizer.withDefaults())

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(oidcLogout)
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )

            // Mantém CSRF, mas ignora /logout se você precisar chamar via GET/POST sem token
            .csrf(csrf -> csrf.ignoringRequestMatchers("/logout"));

        return http.build();
    }
}
