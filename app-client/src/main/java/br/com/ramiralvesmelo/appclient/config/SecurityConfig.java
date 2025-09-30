package br.com.ramiralvesmelo.appclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	SecurityFilterChain security(HttpSecurity http, ClientRegistrationRepository clients) throws Exception {
		var oidcLogout = new OidcClientInitiatedLogoutSuccessHandler(clients);
		// para onde voltar depois do logout no Keycloak
		oidcLogout.setPostLogoutRedirectUri("{baseUrl}/");

		http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/", "/index.html", "/public/**", "/css/**", "/js/**", "/images/**", "/webjars/**",
						"/actuator/health", "/error", "/login", "/login/**", "/oauth2/**", "/login/oauth2/**")
				.permitAll().anyRequest().authenticated()).oauth2Login(oauth -> oauth.defaultSuccessUrl("/", true))
				.oauth2Client(Customizer.withDefaults())
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessHandler(oidcLogout).clearAuthentication(true)
						.invalidateHttpSession(true).deleteCookies("JSESSIONID"))
				.csrf(csrf -> csrf.ignoringRequestMatchers("/logout"));

		return http.build();
	}
}
