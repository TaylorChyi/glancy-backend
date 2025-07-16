package com.glancy.backend.client.prompt;

import com.glancy.backend.entity.Language;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

/**
 * Prompt strategy for French definitions.
 */
@Component
public class FrenchPromptStrategy implements PromptStrategy {
    @Override
    public boolean supports(Language language) {
        return language == Language.FRENCH;
    }

    @Override
    public List<Map<String, String>> buildMessages(String term, Language language) {
        return List.of(
            Map.of("role", "system", "content", "Vous êtes un assistant de dictionnaire."),
            Map.of("role", "user", "content",
                "Fournis la définition de '" + term + "' en français au format:\n" +
                "Définition: ...\nSynonymes: ...\n" +
                "Les synonymes doivent être séparés par des virgules.")
        );
    }
}
