package com.glancy.backend.service;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import com.glancy.backend.client.DeepSeekClient;
import com.glancy.backend.client.GoogleTtsClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Performs dictionary lookups via the configured third-party client.
 */
@Slf4j
@Service
public class WordService {
    private final DeepSeekClient deepSeekClient;
    private final GoogleTtsClient googleTtsClient;

    public WordService(DeepSeekClient deepSeekClient,
                       GoogleTtsClient googleTtsClient) {
        this.deepSeekClient = deepSeekClient;
        this.googleTtsClient = googleTtsClient;
    }

    /**
     * Retrieve word details from the external API.
     */
    @Transactional(readOnly = true)
    public WordResponse findWord(String term, Language language) {
        log.info("Fetching definition for term '{}' in language {}", term, language);
        return deepSeekClient.fetchDefinition(term, language);
    }

    @Transactional(readOnly = true)
    public byte[] getAudio(String term, Language language) {
        log.info("Fetching audio for term '{}' in language {}", term, language);
        return deepSeekClient.fetchAudio(term, language);

    }

    /**
     * Retrieve pronunciation audio bytes from Google TTS.
     */
    @Transactional(readOnly = true)
    public byte[] getPronunciation(String term, Language language) {
        log.info("Fetching pronunciation for term '{}' in language {}", term, language);
        return googleTtsClient.fetchPronunciation(term, language);
    }
}
