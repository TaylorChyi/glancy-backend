package com.glancy.backend.service.dictionary;

import com.glancy.backend.client.DictionaryClient;
import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Strategy using the QianWen API.
 */
@Component
public class QianWenStrategy implements DictionaryStrategy {
    private final DictionaryClient dictionaryClient;

    public QianWenStrategy(@Qualifier("qianWenClient") DictionaryClient dictionaryClient) {
        this.dictionaryClient = dictionaryClient;
    }

    @Override
    public WordResponse fetch(String term, Language language) {
        return dictionaryClient.fetchDefinition(term, language);
    }
}
