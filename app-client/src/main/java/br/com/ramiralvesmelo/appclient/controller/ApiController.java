package br.com.ramiralvesmelo.appclient.controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ApiController {

    private final WebClient.Builder webClientBuilder;
    private final OAuth2AuthorizedClientManager authorizedClientManager;

    @Value("${app.api.base}")
    private String apiBase;

    @GetMapping("/")
    public String home() {
        // Log simples de acesso à página inicial
        log.info("Acessando página inicial");
        return "index";
    }

    @GetMapping("/call-api")
    public String callApi(Authentication authentication, Model model) {
        try {
            log.info("Iniciando chamada à API remota em {}", apiBase);

            // Monta a requisição de autorização do cliente OAuth2
            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                    .withClientRegistrationId("keycloak")
                    .principal(authentication)
                    .build();

            // Obtém o client autorizado (com o access token)
            OAuth2AuthorizedClient client = authorizedClientManager.authorize(authorizeRequest);

            // Extrai o token JWT do Keycloak
            String token = client.getAccessToken().getTokenValue();
            log.debug("Access Token obtido (parcial): {}...", 
                      token.length() > 15 ? token.substring(0, 15) + "..." : token);

            // Faz a chamada à API protegida
            String response = webClientBuilder.build()
                    .get()
                    .uri(apiBase + "/api/hello")
                    .headers(h -> h.setBearerAuth(token))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Chamada à API concluída com sucesso. Resposta recebida.");
            model.addAttribute("apiResponse", response);

        } catch (Exception e) {
            log.error("Erro ao chamar API remota: {}", e.getMessage(), e);
            model.addAttribute("apiError", "Falha ao chamar API: " + e.getMessage());
        }

        return "call-api";
    }


}
