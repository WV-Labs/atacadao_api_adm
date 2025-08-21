package com.mercado.sistema.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("")
public class UploadProxyController {

    @Value("${api.tv.base.url:http://localhost:8081}")
    private String apiTvBaseUrl;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadArquivo(@RequestParam("arquivo") MultipartFile arquivo) {
        Map<String, Object> response = new HashMap<>();

        System.out.println("=== UPLOAD PROXY CONTROLLER ===");
        System.out.println("Arquivo recebido: " + (arquivo != null ? arquivo.getOriginalFilename() : "null"));
        System.out.println("Tamanho: " + (arquivo != null ? arquivo.getSize() : "0"));
        System.out.println("URL API-TV: " + apiTvBaseUrl);

        try {
            // Validações básicas antes de enviar
            if (arquivo.isEmpty()) {
                response.put("erro", "Arquivo não pode estar vazio");
                return ResponseEntity.badRequest().body(response);
            }

            // Validar tipo de arquivo
            String contentType = arquivo.getContentType();
            if (!isValidFileType(contentType)) {
                response.put("erro", "Tipo de arquivo não permitido");
                return ResponseEntity.badRequest().body(response);
            }

            // Preparar dados para envio à api-tv
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Converter MultipartFile para ByteArrayResource para reenvio
            ByteArrayResource resource = new ByteArrayResource(arquivo.getBytes()) {
                @Override
                public String getFilename() {
                    return arquivo.getOriginalFilename();
                }
            };

            // Criar body da requisição
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("arquivo", resource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Fazer requisição para api-tv (context path já inclui /api-tv)
            String urlApiTv = apiTvBaseUrl + "/api-tv/upload";

            System.out.println("Enviando para: " + urlApiTv);

            ResponseEntity<Map> responseApiTv = restTemplate.exchange(
                    urlApiTv,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            System.out.println("Resposta da API-TV: " + responseApiTv.getBody());

            // Retornar resposta da api-tv
            return ResponseEntity.status(responseApiTv.getStatusCode()).body(responseApiTv.getBody());

        } catch (IOException e) {
            System.err.println("Erro de IO: " + e.getMessage());
            response.put("erro", "Erro ao processar arquivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            System.err.println("Erro na comunicação: " + e.getMessage());
            e.printStackTrace();
            response.put("erro", "Erro ao conectar com api-tv: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/upload/{nomeArquivo}")
    public ResponseEntity<Map<String, Object>> deletarArquivo(@PathVariable String nomeArquivo) {
        try {
            // Redirecionar delete para api-tv
            String urlApiTv = apiTvBaseUrl + "/api-tv/upload/" + nomeArquivo;

            System.out.println("Deletando arquivo via: " + urlApiTv);

            ResponseEntity<Map> responseApiTv = restTemplate.exchange(
                    urlApiTv,
                    HttpMethod.DELETE,
                    null,
                    Map.class
            );

            return ResponseEntity.status(responseApiTv.getStatusCode()).body(responseApiTv.getBody());

        } catch (Exception e) {
            System.err.println("Erro ao deletar arquivo: " + e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("erro", "Erro ao conectar com api-tv: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private boolean isValidFileType(String contentType) {
        if (contentType == null) return false;

        return contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("video/mp4") ||
                contentType.equals("video/mov") ||
                contentType.equals("video/avi") ||
                contentType.equals("video/quicktime");
    }
}