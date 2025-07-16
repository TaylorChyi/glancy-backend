package com.glancy.backend.service.dictionary;

import com.glancy.backend.client.ChatGptClient;
import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import org.springframework.stereotype.Component;

/**
 * Strategy using the ChatGPT API.
 */
@Component
public class ChatGptStrategy implements DictionaryStrategy {
    private final ChatGptClient chatGptClient;

    public ChatGptStrategy(ChatGptClient chatGptClient) {
        this.chatGptClient = chatGptClient;
    }

    @Override
    public WordResponse fetch(String term, Language language) {
        return chatGptClient.fetchDefinition(term, language);
    }
}
