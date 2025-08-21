package com.mercado.sistema.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @Value("${api.tv.base.url:http://localhost:8081}")
    private String apiTvBaseUrl;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/connection")
    public Map<String, Object> testConnection() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Testar conexão com api-tv usando endpoint de health
            String urlTest = apiTvBaseUrl + "/api-tv/health";
            Map result = restTemplate.getForObject(urlTest, Map.class);

            response.put("status", "success");
            response.put("api_tv_url", apiTvBaseUrl);
            response.put("api_tv_response", result);
            response.put("message", "Conexão com API-TV OK");

        } catch (Exception e) {
            response.put("status", "error");
            response.put("api_tv_url", apiTvBaseUrl);
            response.put("error", e.getMessage());
            response.put("message", "Erro na conexão com API-TV - Verifique se a API-TV está rodando na porta 8081");

            // Informações adicionais de debug
            response.put("expected_url", apiTvBaseUrl + "/api-tv/health");
            response.put("troubleshooting", Map.of(
                    "1", "Verifique se a API-TV está rodando: http://localhost:8081/api-tv/health",
                    "2", "Verifique se a porta 8081 está livre",
                    "3", "Verifique se o context-path /api-tv está correto",
                    "4", "Verifique os logs da API-TV"
            ));
        }

        return response;
    }

    @GetMapping("/upload-test")
    public Map<String, Object> testUploadEndpoint() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Testar se o endpoint de upload da api-tv responde
            String urlTest = apiTvBaseUrl + "/api-tv/upload";

            response.put("status", "info");
            response.put("upload_endpoint", urlTest);
            response.put("message", "Endpoint de upload configurado. Teste com arquivo real.");

        } catch (Exception e) {
            response.put("status", "error");
            response.put("error", e.getMessage());
        }

        return response;
    }
}