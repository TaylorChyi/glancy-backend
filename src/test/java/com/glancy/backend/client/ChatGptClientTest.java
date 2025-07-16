package com.glancy.backend.client;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ChatGptClientTest {
    private MockRestServiceServer server;
    private ChatGptClient client;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        client = new ChatGptClient(restTemplate, "http://mock", "");
    }

    @Test
    void spanishPromptIsUsed() {
        server.expect(requestTo("http://mock/chat/completions"))
                .andExpect(method(POST))
                .andExpect(content().string(containsString("Explica 'hola' en español")))
                .andRespond(withSuccess("{\"choices\":[{\"message\":{\"content\":\"ok\"}}]}", MediaType.APPLICATION_JSON));

        WordResponse resp = client.fetchDefinition("hola", Language.SPANISH);
        assertEquals("ok", resp.getDefinitions().get(0));
        server.verify();
    }
}
