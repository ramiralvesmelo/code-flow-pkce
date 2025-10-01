package br.com.ramiralvesmelo.appclient.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.ui.Model;

@Slf4j
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
        log.info("Acessando página inicial");
        return "index";
    }

    @GetMapping("/call-api")
    public String callApi(@RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient client,
                          Model model) {
        log.debug("Chamando API remota em: {}", apiBase);

        String token = client.getAccessToken().getTokenValue();
        String tokenMask = token != null && token.length() >= 15 ? token.substring(0, 15) + "..." : "<null>";
        log.trace("Access Token (parcial): {}", tokenMask);

        try {
            // Se quiser também o status, use toEntity(String.class)
            String response = webClient.get()
                .uri(apiBase + "/api/hello")
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(String.class)
                .block();

            log.info("Resposta da API: {}", response);
            model.addAttribute("apiResponse", response);
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            // Erros HTTP 4xx/5xx com corpo
            String body = e.getResponseBodyAsString();
            log.error("Falha HTTP ao chamar {} - status={} motivo={} corpo={}",
                    apiBase + "/api/hello", e.getStatusCode().value(), e.getStatusText(), body, e);
            if (log.isDebugEnabled()) {
                log.debug("Headers de resposta: {}", e.getHeaders());
            }
            model.addAttribute("apiError",
                    "Erro HTTP " + e.getStatusCode().value() + " ao chamar a API: " + e.getStatusText());
            model.addAttribute("apiErrorBody", body);
        } catch (org.springframework.web.reactive.function.client.WebClientRequestException e) {
            // Problemas de rede/DNS/timeout antes de obter resposta HTTP
            log.error("Falha de requisição ao chamar {} - causa={}: {}",
                    apiBase + "/api/hello",
                    e.getClass().getSimpleName(),
                    e.getMessage(), e);
            model.addAttribute("apiError",
                    "Falha de conexão ao chamar a API (rede/timeout): " + e.getMessage());
        } catch (Exception e) {
            // Qualquer outra falha inesperada
            log.error("Erro inesperado ao chamar {}: {}", apiBase + "/api/hello", e.getMessage(), e);
            model.addAttribute("apiError", "Erro inesperado ao chamar a API: " + e.getMessage());
        }

        return "call-api";
    }

}
