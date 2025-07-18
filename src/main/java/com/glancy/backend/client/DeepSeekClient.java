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
                .path("/words/definition")
                .queryParam("term", term)
                .queryParam("language", language.name().toLowerCase())
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        if (apiKey != null && !apiKey.isEmpty()) {
            headers.setBearerAuth(apiKey);
        }
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<WordResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
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
