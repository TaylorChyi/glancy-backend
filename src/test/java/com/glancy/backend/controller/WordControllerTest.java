package com.glancy.backend.controller;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import com.glancy.backend.service.AlertService;
import com.glancy.backend.service.SearchRecordService;
import com.glancy.backend.service.WordService;
import com.glancy.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(WordController.class)
@Import({com.glancy.backend.config.SecurityConfig.class,
        com.glancy.backend.config.WebConfig.class,
        com.glancy.backend.config.TokenAuthenticationInterceptor.class})
class WordControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private WordService wordService;
    @MockitoBean
    private SearchRecordService searchRecordService;
    @MockitoBean
    private AlertService alertService;
    @MockitoBean
    private UserService userService;

    @Test
    void testGetWord() throws Exception {
        WordResponse resp = new WordResponse("1", "hello", List.of("g"), Language.ENGLISH, "ex", "həˈloʊ");
        when(wordService.findWordForUser(eq(1L), eq("hello"), eq(Language.ENGLISH))).thenReturn(resp);

        doNothing().when(userService).validateToken(1L, "tkn");

        mockMvc.perform(get("/api/words")
                        .param("userId", "1")
                        .header("X-USER-TOKEN", "tkn")
                        .param("term", "hello")
                        .param("language", "ENGLISH")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.term").value("hello"));
    }


    @Test
    void testGetAudio() throws Exception {
        byte[] data = new byte[] {1, 2, 3};
        when(wordService.getAudio(eq("hello"), eq(Language.ENGLISH))).thenReturn(data);

        mockMvc.perform(get("/api/words/audio")
                        .param("term", "hello")
                        .param("language", "ENGLISH"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
