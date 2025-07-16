package com.glancy.backend.service.dictionary;

import com.glancy.backend.client.DeepSeekClient;
import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import org.springframework.stereotype.Component;

/**
 * Strategy using the DeepSeek API.
 */
@Component
public class DeepSeekStrategy implements DictionaryStrategy {
    private final DeepSeekClient deepSeekClient;

    public DeepSeekStrategy(DeepSeekClient deepSeekClient) {
        this.deepSeekClient = deepSeekClient;
    }

    @Override
    public WordResponse fetch(String term, Language language) {
        return deepSeekClient.fetchDefinition(term, language);
    }
}
