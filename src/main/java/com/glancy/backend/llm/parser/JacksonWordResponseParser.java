package com.glancy.backend.llm.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class JacksonWordResponseParser implements WordResponseParser {
    @Override
    public WordResponse parse(String content, String term, Language language) {
        String json = extractJson(content);
        try {
            ObjectMapper mapper = new ObjectMapper();
            var node = mapper.readTree(json);
            String id = node.path("id").isNull() ? null : node.path("id").asText();
            String parsedTerm = node.path("term").asText(null);
            if (parsedTerm == null || parsedTerm.isEmpty()) {
                parsedTerm = node.path("entry").asText(term);
            }
            List<String> definitions = new ArrayList<>();
            var defsNode = node.path("definitions");
            if (defsNode.isArray()) {
                defsNode.forEach(n -> {
                    String part = n.path("partOfSpeech").asText();
                    var meaningsNode = n.path("meanings");
                    List<String> meanings = new ArrayList<>();
                    if (meaningsNode.isArray()) {
                        meaningsNode.forEach(m -> meanings.add(m.asText()));
                    } else if (n.has("definition")) {
                        meanings.add(n.path("definition").asText());
                    }
                    String combined = String.join("; ", meanings);
                    if (!combined.isEmpty()) {
                        definitions.add(part.isEmpty() ? combined : part + ": " + combined);
                    }
                });
            } else if (defsNode.isTextual()) {
                definitions.add(defsNode.asText());
            }
            String langStr = node.path("language").asText();
            Language lang = language;
            if (!langStr.isEmpty()) {
                String upper = langStr.toUpperCase();
                if (upper.contains("CHINESE")) {
                    lang = Language.CHINESE;
                } else if (upper.contains("ENGLISH")) {
                    lang = Language.ENGLISH;
                } else {
                    try {
                        lang = Language.valueOf(upper);
                    } catch (Exception ignored) {
                    }
                }
            }
            String example = node.path("example").isNull() ? null : node.path("example").asText();
            if ((example == null || example.isEmpty()) && defsNode.isArray()) {
                for (var def : defsNode) {
                    var exNode = def.path("examples");
                    if (exNode.isArray() && exNode.size() > 0) {
                        example = exNode.get(0).asText();
                        break;
                    }
                }
            }
            String phonetic = node.path("phonetic").isNull() ? null : node.path("phonetic").asText();
            if ((phonetic == null || phonetic.isEmpty())) {
                var pronNode = node.path("pronunciations");
                if (pronNode.isObject()) {
                    var it = pronNode.properties();
                    if (it.hasNext()) {
                        phonetic = it.next().getValue().asText();
                    }
                }
            }
            return new WordResponse(id, parsedTerm, definitions, lang, example, phonetic);
        } catch (Exception e) {
            log.warn("Failed to parse word response", e);
            return new WordResponse(null, term, new ArrayList<>(), language, null, null);
        }
    }

    private String extractJson(String text) {
        String trimmed = text.trim();
        if (trimmed.startsWith("```")) {
            int firstNewline = trimmed.indexOf('\n');
            if (firstNewline != -1) {
                trimmed = trimmed.substring(firstNewline + 1);
            }
            int lastFence = trimmed.lastIndexOf("```");
            if (lastFence != -1) {
                trimmed = trimmed.substring(0, lastFence);
            }
        }
        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start != -1 && end != -1 && start < end) {
            trimmed = trimmed.substring(start, end + 1);
        }
        return trimmed.trim();
    }
}
