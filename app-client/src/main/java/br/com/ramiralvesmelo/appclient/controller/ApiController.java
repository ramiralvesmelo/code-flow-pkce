package br.com.ramiralvesmelo.appclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.ui.Model;

@Controller
public class ApiController {

    private final WebClient webClient;
    private final String apiBase;

    public ApiController(WebClient.Builder builder, @Value("${app.api.base}") String apiBase) {
        this.webClient = builder.build();
        this.apiBase = apiBase;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/call-api")
    public String callApi(@RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient client,
                          Model model) {
        String response = webClient.get()
            .uri(apiBase + "/api/hello")
            .headers(h -> h.setBearerAuth(client.getAccessToken().getTokenValue()))
            .retrieve()
            .bodyToMono(String.class)
            .block();

        model.addAttribute("apiResponse", response);
        return "call-api";
    }
}
