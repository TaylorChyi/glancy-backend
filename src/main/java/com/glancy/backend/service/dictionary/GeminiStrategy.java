package com.glancy.backend.service.dictionary;

import com.glancy.backend.client.GeminiClient;
import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import org.springframework.stereotype.Component;

/**
 * Strategy using the Gemini API.
 */
@Component
public class GeminiStrategy implements DictionaryStrategy {
    private final GeminiClient geminiClient;

    public GeminiStrategy(GeminiClient geminiClient) {
        this.geminiClient = geminiClient;
    }

    @Override
    public WordResponse fetch(String term, Language language) {
        return geminiClient.fetchDefinition(term, language);
    }
}
