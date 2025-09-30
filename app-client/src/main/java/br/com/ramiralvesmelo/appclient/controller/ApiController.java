package br.com.ramiralvesmelo.appclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
public class ApiController {

    private final WebClient webClient;
    private final String apiBase;

    public ApiController(WebClient.Builder builder, @Value("${app.api.base}") String apiBase) {
        this.webClient = builder.build();
        this.apiBase = apiBase;
    }

    @GetMapping("/")
    public String home() {
        return """
                <h1>Portal</h1>
                <p><a href="/call-api">Chamar API Protegida</a></p>
                <p><a href="/oauth2/authorization/keycloak">Login</a> | <a href="/logout">Logout</a></p>
                """;
    }

    @GetMapping("/call-api")
    public String callApi(@RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient client) {
        String response = webClient.get()
            .uri(apiBase + "/api/hello")
            .headers(h -> h.setBearerAuth(client.getAccessToken().getTokenValue()))
            .retrieve()
            .bodyToMono(String.class)
            .block();

        return "<h2>Resposta da API:</h2><pre>" + response + "</pre>";
    }
}
