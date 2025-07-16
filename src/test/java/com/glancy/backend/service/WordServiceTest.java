package com.glancy.backend.service;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import com.glancy.backend.client.DeepSeekClient;
import com.glancy.backend.client.ChatGptClient;
import com.glancy.backend.client.GoogleTtsClient;
import com.glancy.backend.client.GeminiClient;
import com.glancy.backend.entity.Word;
import com.glancy.backend.repository.WordRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class WordServiceTest {
    @Autowired
    private WordService wordService;
    @MockBean
    private DeepSeekClient deepSeekClient;
    @MockBean
    private ChatGptClient chatGptClient;
    @MockBean
    private GoogleTtsClient googleTtsClient;
    @MockBean
    private GeminiClient geminiClient;
    @Autowired
    private WordRepository wordRepository;

    @BeforeAll
    static void loadEnv() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String dbPassword = dotenv.get("DB_PASSWORD");
        if (dbPassword != null) {
            System.setProperty("DB_PASSWORD", dbPassword);
        }
    }

    @BeforeEach
    void setUp() {
        wordRepository.deleteAll();
    }


    @Test
    void testFetchAndCacheWord() {
        WordResponse resp = new WordResponse(null, "hello",
                List.of("greeting"), Language.ENGLISH, "Hello world", "həˈloʊ");
        when(deepSeekClient.fetchDefinition("hello", Language.ENGLISH))
                .thenReturn(resp);

        WordResponse result = wordService.findWordFromDeepSeek("hello", Language.ENGLISH);

        assertNotNull(result.getId());
        assertEquals("greeting", result.getDefinitions().get(0));
        verify(deepSeekClient, times(1)).fetchDefinition("hello", Language.ENGLISH);
        assertTrue(wordRepository.findById(result.getId()).isPresent());
    }

    @Test
    void testUseCachedWord() {
        Word word = new Word();
        word.setTerm("cached");
        word.setLanguage(Language.ENGLISH);
        word.setDefinitions(List.of("store"));
        wordRepository.save(word);

        WordResponse result = wordService.findWordFromDeepSeek("cached", Language.ENGLISH);

        assertEquals(word.getId(), result.getId());
        verify(deepSeekClient, never()).fetchDefinition(anyString(), any());
    }

    @Test
    void testFindWordWithGpt() {
        WordResponse resp = new WordResponse(null, "hi",
                List.of("salutation"), Language.ENGLISH, null, null);
        when(chatGptClient.fetchDefinition("hi", Language.ENGLISH))
                .thenReturn(resp);

        WordResponse result = wordService.findWordWithGpt("hi", Language.ENGLISH);
        assertEquals("salutation", result.getDefinitions().get(0));
    }

    @Test
    void testGetPronunciation() {
        byte[] data = new byte[] {1, 2, 3};
        when(googleTtsClient.fetchPronunciation("hi", Language.ENGLISH))
                .thenReturn(data);

        byte[] result = wordService.getPronunciation("hi", Language.ENGLISH);
        assertArrayEquals(data, result);
    }

    @Test
    void testFindWordFromGemini() {
        WordResponse resp = new WordResponse(1L, "hello",
                List.of("salutation"), Language.ENGLISH, "Hello world", "həˈloʊ");
        when(geminiClient.fetchDefinition("hello", Language.ENGLISH))
                .thenReturn(resp);

        WordResponse result = wordService.findWordFromGemini("hello", Language.ENGLISH);
        assertEquals(resp, result);
    }

    @Test
    void testGetAudio() {
        byte[] data = new byte[] {1, 2, 3};
        when(deepSeekClient.fetchAudio("hello", Language.ENGLISH)).thenReturn(data);

        byte[] result = wordService.getAudio("hello", Language.ENGLISH);
        assertArrayEquals(data, result);
    }
}
