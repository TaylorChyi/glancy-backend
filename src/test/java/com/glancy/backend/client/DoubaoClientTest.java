package com.glancy.backend.client;

import com.glancy.backend.llm.model.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionResponse;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionResponse.Choice;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionResponse.Message;
import com.volcengine.ark.runtime.service.ArkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DoubaoClientTest {
    private ArkService service;
    private DoubaoClient client;

    @BeforeEach
    void setUp() {
        service = mock(ArkService.class);
        client = new DoubaoClient(service);
    }

    @Test
    void chatReturnsContent() {
        ChatCompletionResponse resp = new ChatCompletionResponse();
        Choice choice = new Choice();
        Message msg = new Message();
        msg.setContent("hi");
        choice.setMessage(msg);
        resp.setChoices(List.of(choice));
        when(service.createChatCompletion(any(ChatCompletionRequest.class))).thenReturn(resp);

        String result = client.chat(List.of(new ChatMessage("user", "hi")), 0.5);

        ArgumentCaptor<ChatCompletionRequest> cap = ArgumentCaptor.forClass(ChatCompletionRequest.class);
        verify(service).createChatCompletion(cap.capture());
        assertEquals("hi", result);
    }
}

