package com.glancy.backend.client;

import com.glancy.backend.llm.model.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class DoubaoClientTest {
    private MockRestServiceServer server;
    private DoubaoClient client;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        client = new DoubaoClient(restTemplate, "http://mock", "key");
    }

    @Test
    void chatReturnsContent() {
        String json = "{\"choices\":[{\"message\":{\"role\":\"assistant\",\"content\":\"hi\"}}]}";
        server.expect(requestTo("http://mock/v1/chat/completions"))
                .andExpect(method(POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer key"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        String result = client.chat(List.of(new ChatMessage("user", "hi")), 0.5);
        assertEquals("hi", result);
        server.verify();
    }
}
