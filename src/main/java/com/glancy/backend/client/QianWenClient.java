package com.glancy.backend.client;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Client for interacting with the Qianwen API.
 */
@Component
public class QianWenClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public QianWenClient(RestTemplate restTemplate,
                         @Value("${thirdparty.qianwen.base-url:https://api.qianwen.com}") String baseUrl) {
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
}
