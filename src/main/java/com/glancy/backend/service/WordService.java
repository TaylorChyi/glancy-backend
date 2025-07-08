package com.glancy.backend.service;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import com.glancy.backend.client.DeepSeekClient;
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

    public WordService(DeepSeekClient deepSeekClient) {
        this.deepSeekClient = deepSeekClient;
    }

    /**
     * Retrieve word details from the external API.
     */
    @Transactional(readOnly = true)
    public WordResponse findWord(String term, Language language) {
        log.info("Fetching definition for term '{}' in language {}", term, language);
        return deepSeekClient.fetchDefinition(term, language);
    }
}
