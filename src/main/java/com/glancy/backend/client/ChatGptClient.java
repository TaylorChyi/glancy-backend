package com.glancy.backend.client;

import com.glancy.backend.dto.ChatGptResponse;
import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ChatGptClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;

    public ChatGptClient(RestTemplate restTemplate,
                         @Value("${thirdparty.openai.base-url:https://api.openai.com/v1}") String baseUrl,
                         @Value("${thirdparty.openai.api-key:}") String apiKey) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public WordResponse fetchDefinition(String term, Language language) {
        String url = baseUrl + "/chat/completions";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (apiKey != null && !apiKey.isEmpty()) {
            headers.setBearerAuth(apiKey);
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "gpt-3.5-turbo");
        List<Map<String, String>> messages;
        if (language == Language.SPANISH) {
            messages = List.of(
                Map.of("role", "system", "content", "Eres un asistente de diccionario."),
                Map.of("role", "user", "content",
                    "Explica '" + term + "' en español. " +
                    "Responde con el formato:\nDefinición: <texto>\nSinónimos: <lista separada por comas>")
            );
        } else if (language == Language.FRENCH) {
            messages = List.of(
                Map.of("role", "system", "content", "Vous êtes un assistant de dictionnaire."),
                Map.of("role", "user", "content",
                    "Fournis la définition de '" + term + "' en français au format:\n" +
                    "Définition: ...\nSynonymes: ...\n" +
                    "Les synonymes doivent être séparés par des virgules.")
            );
        } else {
            messages = List.of(
                Map.of("role", "system", "content", "You are a dictionary assistant."),
                Map.of("role", "user", "content",
                    "Define '" + term + "' in " + language.name().toLowerCase() +
                    " and provide synonyms separated by comma.")
            );
        }
        payload.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        ChatGptResponse resp = restTemplate.postForObject(url, entity, ChatGptResponse.class);
        String content = "";
        if (resp != null && resp.getChoices() != null && !resp.getChoices().isEmpty()) {
            content = resp.getChoices().get(0).getMessage().getContent();
        }
        return new WordResponse(null, term, List.of(content), language, null, null);
    }
}
