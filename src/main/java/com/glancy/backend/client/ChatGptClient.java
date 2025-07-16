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
import com.glancy.backend.client.prompt.PromptStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ChatGptClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;
    private final List<PromptStrategy> promptStrategies;

    public ChatGptClient(RestTemplate restTemplate,
                         @Value("${thirdparty.openai.base-url:https://api.openai.com/v1}") String baseUrl,
                         @Value("${thirdparty.openai.api-key:}") String apiKey,
                         List<PromptStrategy> promptStrategies) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.promptStrategies = promptStrategies;
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
        List<Map<String, String>> messages = promptStrategies.stream()
                .filter(s -> s.supports(language))
                .findFirst()
                .orElseThrow()
                .buildMessages(term, language);
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
