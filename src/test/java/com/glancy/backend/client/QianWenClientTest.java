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

class QianWenClientTest {
    private MockRestServiceServer server;
    private QianWenClient client;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        client = new QianWenClient(restTemplate, "http://mock");
    }

    @Test
    void fetchDefinition() {
        String json = "{\"id\":null,\"term\":\"hi\",\"definitions\":[\"hello\"],\"language\":\"CHINESE\",\"example\":null,\"phonetic\":null}";
        server.expect(requestTo("http://mock/words/definition?term=hi&language=chinese"))
                .andExpect(method(GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        WordResponse resp = client.fetchDefinition("hi", Language.CHINESE);
        assertEquals("hello", resp.getDefinitions().get(0));
        server.verify();
    }
}
