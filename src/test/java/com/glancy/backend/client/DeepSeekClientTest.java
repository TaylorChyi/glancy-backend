package com.glancy.backend.client;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class DeepSeekClientTest {
    private MockRestServiceServer server;
    private DeepSeekClient client;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        client = new DeepSeekClient(restTemplate, "http://mock", "key");
    }

    @Test
    void fetchDefinitionWithAuth() {
        String content = "{\\\"id\\\":null,\\\"term\\\":\\\"hello\\\",\\\"definitions\\\":[\\\"hi\\\"],\\\"language\\\":\\\"ENGLISH\\\",\\\"example\\\":null,\\\"phonetic\\\":null}";
        String json = "{\"choices\":[{\"message\":{\"role\":\"assistant\",\"content\":\"" + content + "\"}}]}";
        server.expect(requestTo("http://mock/v1/chat/completions"))
                .andExpect(method(POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer key"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.messages[1].content").value("hello"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        WordResponse resp = client.fetchDefinition("hello", Language.ENGLISH);
        assertNotNull(resp);
        server.verify();
    }

    @Test
    void fetchDefinitionWithCodeFence() {
        String content = "```json\n{\\\"id\\\":null,\\\"term\\\":\\\"hi\\\",\\\"definitions\\\":[\\\"hey\\\"],\\\"language\\\":\\\"ENGLISH\\\"}\n```";
        String json = "{\"choices\":[{\"message\":{\"role\":\"assistant\",\"content\":\"" + content + "\"}}]}";
        server.expect(requestTo("http://mock/v1/chat/completions"))
                .andExpect(method(POST))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        WordResponse resp = client.fetchDefinition("hi", Language.ENGLISH);
        assertEquals("hi", resp.getTerm());
        assertEquals("ENGLISH", resp.getLanguage().name());
        server.verify();
    }

    @Test
    void fetchDefinitionWithNonStandardLanguage() {
        String content = "{\\\"id\\\":null,\\\"term\\\":\\\"\u770B\u770B\\\",\\\"definitions\\\":[\\\"look\\\"],\\\"language\\\":\\\"Chinese (Mandarin)\\\"}";
        String json = "{\"choices\":[{\"message\":{\"role\":\"assistant\",\"content\":\"" + content + "\"}}]}";
        server.expect(requestTo("http://mock/v1/chat/completions"))
                .andExpect(method(POST))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        WordResponse resp = client.fetchDefinition("看看", Language.CHINESE);
        assertEquals(Language.CHINESE, resp.getLanguage());
        assertEquals("看看", resp.getTerm());
        server.verify();
    }

    @Test
    void fetchAudioWithAuth() {
        byte[] audio = new byte[] {1};
        server.expect(requestTo("http://mock/words/audio?term=hello&language=english"))
                .andExpect(method(GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer key"))
                .andRespond(withSuccess(audio, MediaType.APPLICATION_OCTET_STREAM));

        byte[] resp = client.fetchAudio("hello", Language.ENGLISH);
        assertArrayEquals(audio, resp);
        server.verify();
    }

    @Disabled("Requires network access to DeepSeek API")
    @Test
    void callDeepSeekApiExample() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String key = dotenv.get("thirdparty.deepseek.api-key");
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (key != null && !key.isEmpty()) {
            headers.setBearerAuth(key);
        }

        String body = """
            {"model":"deepseek-chat","messages":[{"role":"system","content":"You are a helpful assistant."},{"role":"user","content":"介绍一下牛顿第一定律"}],"temperature":0.7,"stream":true}
            """;
        var entity = new org.springframework.http.HttpEntity<>(body, headers);
        var response = rt.postForEntity("https://api.deepseek.com/v1/chat/completions", entity, String.class);
        System.out.println(response.getBody());
    }
}
