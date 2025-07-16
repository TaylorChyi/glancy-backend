package com.glancy.backend.client.prompt;

import com.glancy.backend.entity.Language;
import java.util.List;
import java.util.Map;

/**
 * Strategy interface for building ChatGPT prompts.
 */
public interface PromptStrategy {
    /**
     * Whether this strategy supports the given language.
     */
    boolean supports(Language language);

    /**
     * Build the message list for the specified term and language.
     */
    List<Map<String, String>> buildMessages(String term, Language language);
}
