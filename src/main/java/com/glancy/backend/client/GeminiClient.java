package com.glancy.backend.client;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Client for retrieving word definitions from the Gemini API.
 */
@Component
public class GeminiClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public GeminiClient(RestTemplate restTemplate,
                        @Value("${thirdparty.gemini.base-url:https://api.gemini.com}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    /**
     * Fetch word definition from Gemini.
     */
    public WordResponse fetchDefinition(String term, Language language) {
        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/words/definition")
                .queryParam("term", term)
                .queryParam("language", language.name().toLowerCase())
                .toUriString();
        return restTemplate.getForObject(url, WordResponse.class);
    }
}
