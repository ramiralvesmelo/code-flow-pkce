package br.com.ramiralvesmelo.appclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
  @Bean
  SecurityFilterChain security(HttpSecurity http) throws Exception {
    http
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/", "/public/**", "/actuator/health").permitAll()
        .anyRequest().authenticated()
      )
      .oauth2Login(oauth -> oauth.loginPage("/oauth2/authorization/keycloak")) // opcional
      .oauth2Client(client -> {})
      .logout(logout -> logout.logoutSuccessUrl("/").invalidateHttpSession(true).clearAuthentication(true));
    return http.build();
  }
}
