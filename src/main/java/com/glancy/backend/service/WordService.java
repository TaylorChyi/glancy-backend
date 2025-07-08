package com.glancy.backend.service;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import com.glancy.backend.client.DeepSeekClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WordService {
    private final DeepSeekClient deepSeekClient;

    public WordService(DeepSeekClient deepSeekClient) {
        this.deepSeekClient = deepSeekClient;
    }

    @Transactional(readOnly = true)
    public WordResponse findWord(String term, Language language) {
        return deepSeekClient.fetchDefinition(term, language);
    }
}
