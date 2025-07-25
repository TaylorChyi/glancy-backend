package com.glancy.backend.client;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.glancy.backend.client.DictionaryClient;

/**
 * Client for interacting with the Qianwen API.
 */
@Slf4j
@Component("qianWenClient")
public class QianWenClient implements DictionaryClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public QianWenClient(RestTemplate restTemplate,
                         @Value("${thirdparty.qianwen.base-url:https://api.qianwen.com}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public WordResponse fetchDefinition(String term, Language language) {
        log.info("Entering fetchDefinition with term '{}' and language {}", term, language);
        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/words/definition")
                .queryParam("term", term)
                .queryParam("language", language.name().toLowerCase())
                .toUriString();
        return restTemplate.getForObject(url, WordResponse.class);
    }

    @Override
    public byte[] fetchAudio(String term, Language language) {
        throw new UnsupportedOperationException("Audio fetch not supported");
    }
}
