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
                parsedTerm = node.path("entry").asText(null);
            }
            if (parsedTerm == null || parsedTerm.isEmpty()) {
                parsedTerm = node.path("\u8BCD\u6761").asText(term); // "词条"
            }

            List<String> definitions = parseEnglishDefinitions(node);
            if (definitions.isEmpty()) {
                definitions = parseChineseDefinitions(node);
            }

            Language lang = parseLanguage(node, language);

            String example = parseExample(node, definitions);

            String phonetic = parsePhonetic(node);

            return new WordResponse(id, parsedTerm, definitions, lang, example, phonetic);
        } catch (Exception e) {
            log.warn("Failed to parse word response", e);
            return new WordResponse(null, term, new ArrayList<>(), language, null, null);
        }
    }

    private List<String> parseEnglishDefinitions(com.fasterxml.jackson.databind.JsonNode node) {
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
        return definitions;
    }

    private List<String> parseChineseDefinitions(com.fasterxml.jackson.databind.JsonNode node) {
        List<String> definitions = new ArrayList<>();
        var explainNode = node.path("\u53D1\u97F3\u89E3\u91CA"); // "发音解释"
        if (explainNode.isArray()) {
            for (var exp : explainNode) {
                var defList = exp.path("\u91CA\u4E49"); // "释义"
                if (defList.isArray()) {
                    for (var d : defList) {
                        String def = d.path("\u5B9A\u4E49").asText(); // "定义"
                        String part = d.path("\u7C7B\u522B").asText(); // "类别"
                        String combined = def;
                        if (!part.isEmpty()) {
                            combined = part + ": " + def;
                        }
                        if (!combined.isEmpty()) {
                            definitions.add(combined);
                        }
                    }
                }
            }
        }
        return definitions;
    }

    private Language parseLanguage(com.fasterxml.jackson.databind.JsonNode node, Language fallback) {
        String langStr = node.path("language").asText();
        if (langStr.isEmpty()) {
            langStr = node.path("\u8BED\u8A00").asText(); // "语言"
        }
        Language lang = fallback;
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
        return lang;
    }

    private String parseExample(com.fasterxml.jackson.databind.JsonNode node, List<String> englishDefs) {
        String example = node.path("example").isNull() ? null : node.path("example").asText();
        var defsNode = node.path("definitions");
        if ((example == null || example.isEmpty()) && defsNode.isArray()) {
            for (var def : defsNode) {
                var exNode = def.path("examples");
                if (exNode.isArray() && exNode.size() > 0) {
                    example = exNode.get(0).asText();
                    break;
                }
            }
        }
        if (example == null || example.isEmpty()) {
            var explainNode = node.path("\u53D1\u97F3\u89E3\u91CA"); // "发音解释"
            if (explainNode.isArray()) {
                for (var exp : explainNode) {
                    var defList = exp.path("\u91CA\u4E49"); // "释义"
                    if (defList.isArray()) {
                        for (var d : defList) {
                            var exNode = d.path("\u4F8B\u53E5"); // "例句"
                            if (exNode.isArray() && exNode.size() > 0) {
                                var first = exNode.get(0);
                                if (first.has("\u6E90\u8BED\u8A00")) { // "源语言"
                                    example = first.path("\u6E90\u8BED\u8A00").asText();
                                } else if (first.isTextual()) {
                                    example = first.asText();
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        return example;
    }

    private String parsePhonetic(com.fasterxml.jackson.databind.JsonNode node) {
        String phonetic = node.path("phonetic").isNull() ? null : node.path("phonetic").asText();
        if (phonetic == null || phonetic.isEmpty()) {
            var pronNode = node.path("pronunciations");
            if (pronNode.isObject()) {
                var fieldNames = pronNode.fieldNames();
                if (fieldNames.hasNext()) {
                    String firstKey = fieldNames.next();
                    var value = pronNode.get(firstKey);
                    if (value != null && !value.isNull()) {
                        phonetic = value.asText();
                    }
                }
            }
        }
        if (phonetic == null || phonetic.isEmpty()) {
            var pNode = node.path("\u53D1\u97F3"); // "发音"
            if (pNode.isObject()) {
                if (pNode.has("\u82F1\u97F3")) {
                    phonetic = pNode.path("\u82F1\u97F3").asText();
                } else if (pNode.has("\u7F8E\u97F3")) {
                    phonetic = pNode.path("\u7F8E\u97F3").asText();
                }
            }
        }
        return phonetic;
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
