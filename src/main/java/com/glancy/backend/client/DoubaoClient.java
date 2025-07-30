package com.glancy.backend.client;

import com.glancy.backend.llm.llm.LLMClient;
import com.glancy.backend.llm.model.ChatMessage;
import com.glancy.backend.entity.LlmModel;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionResponse;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import jakarta.annotation.PreDestroy;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component("doubaoClient")
public class DoubaoClient implements LLMClient {
    private final ArkService service;

    public DoubaoClient(@Value("${thirdparty.doubao.api-key:}") String apiKey,
                         @Value("${thirdparty.doubao.base-url:https://ark.cn-beijing.volces.com/api/v3}") String baseUrl) {
        this.service = ArkService.builder()
                .dispatcher(new Dispatcher())
                .connectionPool(new ConnectionPool(5, 1, TimeUnit.SECONDS))
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
    }

    DoubaoClient(ArkService service) {
        this.service = service;
    }

    @Override
    public String name() {
        return "doubao";
    }

    @Override
    public String chat(List<ChatMessage> messages, double temperature) {
        List<com.volcengine.ark.runtime.model.completion.chat.ChatMessage> reqMessages = new ArrayList<>();
        for (ChatMessage m : messages) {
            reqMessages.add(com.volcengine.ark.runtime.model.completion.chat.ChatMessage.builder()
                    .role(ChatMessageRole.valueOf(m.getRole().toUpperCase()))
                    .content(m.getContent())
                    .build());
        }
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(LlmModel.DOUBAO_FLASH.getModelName())
                .messages(reqMessages)
                .temperature(temperature)
                .build();
        ChatCompletionResponse response = service.createChatCompletion(request);
        return response.getChoices().get(0).getMessage().getContent();
    }

    @PreDestroy
    public void shutdown() {
        service.shutdownExecutor();
    }
}
