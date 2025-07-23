package com.glancy.backend.service;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import com.glancy.backend.client.DeepSeekClient;
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
        when(userPreferenceRepository.findByUserId(anyLong())).thenReturn(java.util.Optional.empty());
    }


    @Test
    void testFetchAndCacheWord() {
        WordResponse resp = new WordResponse(null, "hello",
                List.of("greeting"), Language.ENGLISH, "Hello world", "həˈloʊ");
        when(deepSeekStrategy.fetch("hello", Language.ENGLISH))
                .thenReturn(resp);

        WordResponse result = wordService.findWordForUser(1L, "hello", Language.ENGLISH);

        assertNotNull(result.getId());
        assertEquals("greeting", result.getDefinitions().get(0));
        verify(deepSeekStrategy, times(1)).fetch("hello", Language.ENGLISH);
        assertTrue(wordRepository.findById(Long.parseLong(result.getId())).isPresent());
    }

    @Test
    void testUseCachedWord() {
        Word word = new Word();
        word.setTerm("cached");
        word.setLanguage(Language.ENGLISH);
        word.setDefinitions(List.of("store"));
        wordRepository.save(word);

        WordResponse result = wordService.findWordForUser(1L, "cached", Language.ENGLISH);

        assertEquals(String.valueOf(word.getId()), result.getId());
        verify(deepSeekStrategy, never()).fetch(anyString(), any());
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
    void testCacheWordWhenLanguageMissing() {
        WordResponse resp = new WordResponse(null, "bye", List.of("farewell"), null, null, null);
        when(deepSeekStrategy.fetch("bye", Language.ENGLISH)).thenReturn(resp);

        WordResponse result = wordService.findWordForUser(1L, "bye", Language.ENGLISH);

        assertEquals(Language.ENGLISH, result.getLanguage());
        assertTrue(wordRepository.findByTermAndLanguageAndDeletedFalse("bye", Language.ENGLISH).isPresent());
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
