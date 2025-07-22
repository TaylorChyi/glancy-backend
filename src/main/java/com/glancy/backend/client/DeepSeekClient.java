package com.glancy.backend.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glancy.backend.dto.ChatCompletionResponse;
import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DeepSeekClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;
    private final String enToZhPrompt;
    private final String zhToEnPrompt;

    public DeepSeekClient(RestTemplate restTemplate,
                          @Value("${thirdparty.deepseek.base-url:https://api.deepseek.com}") String baseUrl,
                          @Value("${thirdparty.deepseek.api-key:}") String apiKey) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.enToZhPrompt = loadPrompt("prompts/english_to_chinese.txt");
        this.zhToEnPrompt = loadPrompt("prompts/chinese_to_english.txt");
    }

    private String loadPrompt(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            return StreamUtils.copyToString(resource.getInputStream(), java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("Failed to load prompt {}", path, e);
            return "";
        }
    }

    public WordResponse fetchDefinition(String term, Language language) {
        log.info("Entering fetchDefinition with term '{}' and language {}", term, language);
        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/v1/chat/completions")
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (apiKey != null && !apiKey.isEmpty()) {
            headers.setBearerAuth(apiKey);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", "deepseek-chat");
        body.put("temperature", 0.7);
        body.put("stream", false);

        String systemPrompt = language == Language.ENGLISH ? enToZhPrompt : zhToEnPrompt;
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        messages.add(Map.of("role", "user", "content", term));
        body.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
        );

        try {
            ObjectMapper mapper = new ObjectMapper();
            ChatCompletionResponse chat = mapper.readValue(response.getBody(), ChatCompletionResponse.class);
            String content = chat.getChoices().get(0).getMessage().getContent();
            log.info("DeepSeek response content: {}", content);
            String json = extractJson(content);
            return parseWordResponse(json, term, language);
        } catch (Exception e) {
            log.warn("Failed to parse DeepSeek response", e);
            return new WordResponse(null, term, new ArrayList<>(), language, null, null);
        }
    }

    public byte[] fetchAudio(String term, Language language) {
        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/words/audio")
                .queryParam("term", term)
                .queryParam("language", language.name().toLowerCase())
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        if (apiKey != null && !apiKey.isEmpty()) {
            headers.setBearerAuth(apiKey);
        }
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                byte[].class
        );
        return response.getBody();
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

    private WordResponse parseWordResponse(String json, String term, Language language) throws Exception {
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
                var it = pronNode.fields();
                if (it.hasNext()) {
                    phonetic = it.next().getValue().asText();
                }
            }
        }

        return new WordResponse(id, parsedTerm, definitions, lang, example, phonetic);
    }
}
