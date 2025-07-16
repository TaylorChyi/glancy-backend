package com.glancy.backend.client;

import com.glancy.backend.entity.Language;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GoogleTtsClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public GoogleTtsClient(RestTemplate restTemplate,
                           @Value("${thirdparty.google-tts.base-url:https://translate.google.com}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public byte[] fetchPronunciation(String term, Language language) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/translate_tts")
                .queryParam("ie", "UTF-8")
                .queryParam("client", "tw-ob")
                .queryParam("tl", mapLanguage(language))
                .queryParam("q", term)
                .toUriString();
        return restTemplate.getForObject(url, byte[].class);
    }

    private String mapLanguage(Language language) {
        return switch (language) {
            case CHINESE -> "zh-CN";
            case ENGLISH -> "en";
            case JAPANESE -> "ja";
            case KOREAN -> "ko";
            case RUSSIAN -> "ru";
            case GERMAN -> "de";
            case FRENCH -> "fr";
        };
    }
}
