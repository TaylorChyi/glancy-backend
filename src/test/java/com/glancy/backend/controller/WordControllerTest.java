package com.glancy.backend.controller;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import com.glancy.backend.service.AlertService;
import com.glancy.backend.service.SearchRecordService;
import com.glancy.backend.service.WordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WordController.class)
@Import(com.glancy.backend.config.SecurityConfig.class)
class WordControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private WordService wordService;
    @MockBean
    private SearchRecordService searchRecordService;
    @MockBean
    private AlertService alertService;

    @Test
    void testGetWord() throws Exception {
        WordResponse resp = new WordResponse(1L, "hello", List.of("g"), Language.ENGLISH, "ex", "həˈloʊ");
        when(wordService.findWordFromDeepSeek(eq("hello"), eq(Language.ENGLISH))).thenReturn(resp);

        mockMvc.perform(get("/api/words")
                        .param("userId", "1")
                        .param("term", "hello")
                        .param("language", "ENGLISH")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.term").value("hello"));
    }

    @Test
    void testGetPronunciation() throws Exception {
        byte[] data = new byte[] {1};
        when(wordService.getPronunciation(eq("hi"), eq(Language.ENGLISH))).thenReturn(data);

        mockMvc.perform(get("/api/words/pronunciation")
                        .param("term", "hi")
                        .param("language", "ENGLISH"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAudio() throws Exception {
        byte[] data = new byte[] {1, 2, 3};
        when(wordService.getAudio(eq("hello"), eq(Language.ENGLISH))).thenReturn(data);

        mockMvc.perform(get("/api/words/audio")
                        .param("term", "hello")
                        .param("language", "ENGLISH"))
                .andExpect(status().isOk());
    }
}
