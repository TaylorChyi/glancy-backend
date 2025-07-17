package com.glancy.backend.service.dictionary;

import com.glancy.backend.client.QianWenClient;
import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import org.springframework.stereotype.Component;

/**
 * Strategy using the QianWen API.
 */
@Component
public class QianWenStrategy implements DictionaryStrategy {
    private final QianWenClient qianWenClient;

    public QianWenStrategy(QianWenClient qianWenClient) {
        this.qianWenClient = qianWenClient;
    }

    @Override
    public WordResponse fetch(String term, Language language) {
        return qianWenClient.fetchDefinition(term, language);
    }
}
