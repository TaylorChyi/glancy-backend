package com.glancy.backend.controller;

import com.glancy.backend.dto.SearchRecordRequest;
import com.glancy.backend.dto.SearchRecordResponse;
import com.glancy.backend.entity.Language;
import com.glancy.backend.service.AlertService;
import com.glancy.backend.service.SearchRecordService;
import com.glancy.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchRecordController.class)
@Import(com.glancy.backend.config.SecurityConfig.class)
class SearchRecordControllerTest {
    @MockitoBean
    private AlertService alertService;
    
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private SearchRecordService searchRecordService;
    @MockitoBean
    private UserService userService;

    @Test
    void testCreate() throws Exception {
        SearchRecordResponse resp = new SearchRecordResponse(1L, 1L, "hello", Language.ENGLISH, LocalDateTime.now());
        when(searchRecordService.saveRecord(any(Long.class), any(SearchRecordRequest.class))).thenReturn(resp);

        doNothing().when(userService).validateToken(1L, "tkn");

        mockMvc.perform(post("/api/search-records/user/1")
                .header("X-USER-TOKEN", "tkn")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"term\":\"hello\",\"language\":\"ENGLISH\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.term").value("hello"));
    }

    @Test
    void testList() throws Exception {
        SearchRecordResponse resp = new SearchRecordResponse(1L, 1L, "hello", Language.ENGLISH, LocalDateTime.now());
        when(searchRecordService.getRecords(1L)).thenReturn(Collections.singletonList(resp));

        doNothing().when(userService).validateToken(1L, "tkn");

        mockMvc.perform(get("/api/search-records/user/1")
                .header("X-USER-TOKEN", "tkn"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].term").value("hello"));
    }
}
