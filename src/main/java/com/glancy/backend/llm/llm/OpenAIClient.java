package com.glancy.backend.llm.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glancy.backend.dto.ChatCompletionResponse;
import com.glancy.backend.llm.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class OpenAIClient implements LLMClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;
    private final String apiKey;

    public OpenAIClient(@Value("${thirdparty.openai.base-url:https://api.openai.com}") String baseUrl,
                        @Value("${thirdparty.openai.api-key:}") String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    @Override
    public String name() {
        return "openai";
    }

    @Override
    public String chat(List<ChatMessage> messages, double temperature) {
        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/v1/chat/completions")
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (apiKey != null && !apiKey.isEmpty()) {
            headers.setBearerAuth(apiKey);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("temperature", temperature);
        List<Map<String, String>> messageList = new ArrayList<>();
        for (ChatMessage m : messages) {
            messageList.add(Map.of("role", m.getRole(), "content", m.getContent()));
        }
        body.put("messages", messageList);

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
            return chat.getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            log.warn("Failed to parse OpenAI response", e);
            return "";
        }
    }
}
