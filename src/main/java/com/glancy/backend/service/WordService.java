package com.glancy.backend.service;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import com.glancy.backend.client.DeepSeekClient;
import com.glancy.backend.client.GeminiClient;
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
    private final GeminiClient geminiClient;

    public WordService(DeepSeekClient deepSeekClient, GeminiClient geminiClient) {
        this.deepSeekClient = deepSeekClient;
        this.geminiClient = geminiClient;
    }

    /**
     * Retrieve word details from the external API.
     */
    @Transactional(readOnly = true)
    public WordResponse findWordFromDeepSeek(String term, Language language) {
        log.info("Fetching definition for term '{}' in language {}", term, language);
        return deepSeekClient.fetchDefinition(term, language);
    }

    /**
     * Retrieve word details using the Gemini provider.
     */
    @Transactional(readOnly = true)
    public WordResponse findWordFromGemini(String term, Language language) {
        log.info("Fetching definition from Gemini for term '{}' in language {}", term, language);
        return geminiClient.fetchDefinition(term, language);

    @Transactional(readOnly = true)
    public byte[] getAudioFromDeepSeek(String term, Language language) {
        log.info("Fetching audio for term '{}' in language {}", term, language);
        return deepSeekClient.fetchAudio(term, language);
    }
}
