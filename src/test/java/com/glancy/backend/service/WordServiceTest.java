package com.glancy.backend.service;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import com.glancy.backend.client.DeepSeekClient;
import com.glancy.backend.client.QianWenClient;
import com.glancy.backend.entity.Word;
import com.glancy.backend.repository.WordRepository;
import com.glancy.backend.repository.UserPreferenceRepository;
import com.glancy.backend.entity.UserPreference;
import com.glancy.backend.entity.DictionaryModel;
import com.glancy.backend.service.dictionary.DeepSeekStrategy;
import com.glancy.backend.service.dictionary.QianWenStrategy;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class WordServiceTest {
    @Autowired
    private WordService wordService;
    @MockitoBean
    private DeepSeekClient deepSeekClient;
    @MockitoBean
    private QianWenClient qianWenClient;
    @MockitoBean
    private UserPreferenceRepository userPreferenceRepository;
    @MockitoBean
    private DeepSeekStrategy deepSeekStrategy;
    @MockitoBean
    private QianWenStrategy qianWenStrategy;
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
    void testFindWordFromQianWen() {
        WordResponse resp = new WordResponse(1L, "hello",
                List.of("salutation"), Language.ENGLISH, "Hello world", "həˈloʊ");
        when(qianWenClient.fetchDefinition("hello", Language.ENGLISH))
                .thenReturn(resp);

        WordResponse result = wordService.findWordFromQianWen("hello", Language.ENGLISH);
        assertEquals(resp, result);
    }

    @Test
    void testGetAudio() {
        byte[] data = new byte[] {1, 2, 3};
        when(deepSeekClient.fetchAudio("hello", Language.ENGLISH)).thenReturn(data);

        byte[] result = wordService.getAudio("hello", Language.ENGLISH);
        assertArrayEquals(data, result);
    }


    @Test
    void testFindWordForUserQianWen() {
        UserPreference pref = new UserPreference();
        pref.setDictionaryModel(DictionaryModel.QIANWEN);
        when(userPreferenceRepository.findByUserId(2L)).thenReturn(java.util.Optional.of(pref));

        WordResponse resp = new WordResponse(null, "hi", List.of("hello"), Language.ENGLISH, null, null);
        when(qianWenStrategy.fetch("hi", Language.ENGLISH)).thenReturn(resp);

        WordResponse result = wordService.findWordForUser(2L, "hi", Language.ENGLISH);
        assertEquals(resp, result);
    }

    @Test
    void testSaveSameTermDifferentLanguage() {
        Word wordEn = new Word();
        wordEn.setTerm("hello");
        wordEn.setLanguage(Language.ENGLISH);
        wordEn.setDefinitions(List.of("greet"));
        wordRepository.save(wordEn);

        Word wordZh = new Word();
        wordZh.setTerm("hello");
        wordZh.setLanguage(Language.CHINESE);
        wordZh.setDefinitions(List.of("\u4f60\u597d"));

        assertDoesNotThrow(() -> wordRepository.save(wordZh));

        assertTrue(wordRepository.findByTermAndLanguageAndDeletedFalse("hello", Language.ENGLISH).isPresent());
        assertTrue(wordRepository.findByTermAndLanguageAndDeletedFalse("hello", Language.CHINESE).isPresent());
    }
}
