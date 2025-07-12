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

    public DeepSeekClient(RestTemplate restTemplate,
                          @Value("${deepseek.base-url:https://api.deepseek.com}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public WordResponse fetchDefinition(String term, Language language) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/words/definition")
                .queryParam("term", term)
                .queryParam("language", language.name().toLowerCase())
                .toUriString();
        return restTemplate.getForObject(url, WordResponse.class);
    }

    public byte[] fetchAudio(String term, Language language) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/words/audio")
                .queryParam("term", term)
                .queryParam("language", language.name().toLowerCase())
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
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
