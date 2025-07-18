package com.glancy.backend.client;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GeminiClientTest {
    private MockRestServiceServer server;
    private GeminiClient client;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        client = new GeminiClient(restTemplate, "http://mock");
    }

    @Test
    void fetchDefinition() {
        String json = "{\"id\":null,\"term\":\"hello\",\"definitions\":[\"hi\"],\"language\":\"ENGLISH\",\"example\":null,\"phonetic\":null}";
        server.expect(requestTo("http://mock/words/definition?term=hello&language=english"))
                .andExpect(method(GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        WordResponse resp = client.fetchDefinition("hello", Language.ENGLISH);
        assertEquals("hi", resp.getDefinitions().get(0));
        server.verify();
    }
}
