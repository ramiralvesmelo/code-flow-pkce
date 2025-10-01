package br.com.ramiralvesmelo.appclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
  @Bean
  WebClient webClient(ClientRegistrationRepository clients,
                      OAuth2AuthorizedClientRepository authzRepo) {
    var oauth = new ServletOAuth2AuthorizedClientExchangeFilterFunction(clients, authzRepo);
    oauth.setDefaultOAuth2AuthorizedClient(true); // usa o token do usuário logado
    oauth.setDefaultClientRegistrationId("keycloak");
    return WebClient.builder().apply(oauth.oauth2Configuration()).build();
  }
}
