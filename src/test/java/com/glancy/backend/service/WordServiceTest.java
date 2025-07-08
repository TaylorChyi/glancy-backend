package com.glancy.backend.service;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import com.glancy.backend.entity.Word;
import com.glancy.backend.repository.WordRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class WordServiceTest {
    @Autowired
    private WordService wordService;
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
    void testFindWord() {
        Word w = new Word();
        w.setTerm("hello");
        w.setDefinitions(List.of("greeting"));
        w.setLanguage(Language.ENGLISH);
        w.setExample("Hello world");
        Word saved = wordRepository.save(w);

        WordResponse resp = wordService.findWord("hello", Language.ENGLISH);
        assertEquals(saved.getId(), resp.getId());
        assertEquals("greeting", resp.getDefinitions().get(0));
    }
}
