package com.glancy.backend.service;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import com.glancy.backend.client.DeepSeekClient;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class WordServiceTest {
    @Autowired
    private WordService wordService;
    @MockBean
    private DeepSeekClient deepSeekClient;

    @BeforeAll
    static void loadEnv() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String dbPassword = dotenv.get("DB_PASSWORD");
        if (dbPassword != null) {
            System.setProperty("DB_PASSWORD", dbPassword);
        }
    }


    @Test
    void testFindWord() {
        WordResponse resp = new WordResponse(1L, "hello",
                List.of("greeting"), Language.ENGLISH, "Hello world", "həˈloʊ");
        when(deepSeekClient.fetchDefinition("hello", Language.ENGLISH))
                .thenReturn(resp);

        WordResponse result = wordService.findWord("hello", Language.ENGLISH);
        assertEquals("greeting", result.getDefinitions().get(0));
    }
}
