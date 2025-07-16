package com.glancy.backend.client.prompt;

import com.glancy.backend.entity.Language;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

/**
 * Prompt strategy for Spanish definitions.
 */
@Component
public class SpanishPromptStrategy implements PromptStrategy {
    @Override
    public boolean supports(Language language) {
        return language == Language.SPANISH;
    }

    @Override
    public List<Map<String, String>> buildMessages(String term, Language language) {
        return List.of(
            Map.of("role", "system", "content", "Eres un asistente de diccionario."),
            Map.of("role", "user", "content",
                "Explica '" + term + "' en español. " +
                "Responde con el formato:\nDefinición: <texto>\nSinónimos: <lista separada por comas>")
        );
    }
}
