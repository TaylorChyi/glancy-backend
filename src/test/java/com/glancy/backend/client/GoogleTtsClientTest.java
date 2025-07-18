package com.glancy.backend.client;

import com.glancy.backend.entity.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GoogleTtsClientTest {
    private MockRestServiceServer server;
    private GoogleTtsClient client;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        client = new GoogleTtsClient(restTemplate, "http://mock");
    }

    @Test
    void fetchPronunciationEnglish() {
        byte[] audio = new byte[] {1, 2};
        server.expect(requestTo("http://mock/translate_tts?ie=UTF-8&client=tw-ob&tl=en&q=hello"))
                .andExpect(method(GET))
                .andRespond(withSuccess(audio, MediaType.APPLICATION_OCTET_STREAM));

        byte[] resp = client.fetchPronunciation("hello", Language.ENGLISH);
        assertArrayEquals(audio, resp);
        server.verify();
    }

    @Test
    void fetchPronunciationChinese() {
        byte[] audio = new byte[] {3, 4};
        server.expect(requestTo("http://mock/translate_tts?ie=UTF-8&client=tw-ob&tl=zh-CN&q=nihao"))
                .andExpect(method(GET))
                .andRespond(withSuccess(audio, MediaType.APPLICATION_OCTET_STREAM));

        byte[] resp = client.fetchPronunciation("nihao", Language.CHINESE);
        assertArrayEquals(audio, resp);
        server.verify();
    }
}
