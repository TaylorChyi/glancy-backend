package com.glancy.backend.controller;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import com.glancy.backend.service.WordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WordController.class)
class WordControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private WordService wordService;

    @Test
    void testGetWord() throws Exception {
        WordResponse resp = new WordResponse(1L, "hello", List.of("g"), Language.ENGLISH, "ex", "həˈloʊ");
        when(wordService.findWord(eq("hello"), eq(Language.ENGLISH))).thenReturn(resp);

        mockMvc.perform(get("/api/words")
                        .param("term", "hello")
                        .param("language", "ENGLISH")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.term").value("hello"));
    }
}
