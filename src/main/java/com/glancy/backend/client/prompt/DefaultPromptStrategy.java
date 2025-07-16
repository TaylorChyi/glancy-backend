package com.glancy.backend.client.prompt;

import com.glancy.backend.entity.Language;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

/**
 * Fallback prompt strategy used when no language-specific one matches.
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class DefaultPromptStrategy implements PromptStrategy {
    @Override
    public boolean supports(Language language) {
        return true;
    }

    @Override
    public List<Map<String, String>> buildMessages(String term, Language language) {
        return List.of(
            Map.of("role", "system", "content", "You are a dictionary assistant."),
            Map.of("role", "user", "content",
                "Define '" + term + "' in " + language.name().toLowerCase() +
                " and provide synonyms separated by comma.")
        );
    }
}
