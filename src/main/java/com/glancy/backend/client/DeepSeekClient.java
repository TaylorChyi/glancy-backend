package com.glancy.backend.client;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DeepSeekClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;

    public DeepSeekClient(RestTemplate restTemplate,
                          @Value("${thirdparty.deepseek.base-url:https://api.deepseek.com}") String baseUrl,
                          @Value("${thirdparty.deepseek.api-key:}") String apiKey) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public WordResponse fetchDefinition(String term, Language language) {
        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/v1/chat/completions")
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (apiKey != null && !apiKey.isEmpty()) {
            headers.setBearerAuth(apiKey);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", "deepseek-chat");
        body.put("temperature", 0.7);
        body.put("stream", false);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "You are a helpful assistant."));
        messages.add(Map.of("role", "user", "content", term));
        body.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<WordResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                WordResponse.class
        );
        return response.getBody();
    }

    public byte[] fetchAudio(String term, Language language) {
        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/words/audio")
                .queryParam("term", term)
                .queryParam("language", language.name().toLowerCase())
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        if (apiKey != null && !apiKey.isEmpty()) {
            headers.setBearerAuth(apiKey);
        }
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                byte[].class
        );
        return response.getBody();
    }
}
