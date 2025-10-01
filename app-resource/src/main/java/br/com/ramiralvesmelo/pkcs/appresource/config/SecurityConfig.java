package br.com.ramiralvesmelo.pkcs.appresource.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration 
public class SecurityConfig {

    @Bean
    SecurityFilterChain security(HttpSecurity http) throws Exception {
        http
            // Desabilita CSRF (Cross-Site Request Forgery), porque aqui usamos JWT (stateless, sem sessão)
            .csrf(csrf -> csrf.disable())

            // Define as regras de autorização para as requisições HTTP
            .authorizeHttpRequests(auth -> auth
                // Permite acesso público ao endpoint de health (monitoramento)
                .requestMatchers("/actuator/health").permitAll()

                // Exige autenticação (JWT válido) para qualquer endpoint que comece com /api/
                .requestMatchers("/api/**").authenticated()

                // Qualquer outra requisição também precisa estar autenticada
                .anyRequest().authenticated()
            )

            // Configura o aplicativo como Resource Server baseado em JWT
            // O Spring Security vai validar tokens recebidos no header Authorization
            .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));

        // Retorna a configuração construída
        return http.build();
    }
}
