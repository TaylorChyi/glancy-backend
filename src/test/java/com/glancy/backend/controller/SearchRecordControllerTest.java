package com.glancy.backend.controller;

import com.glancy.backend.dto.SearchRecordRequest;
import com.glancy.backend.dto.SearchRecordResponse;
import com.glancy.backend.entity.Language;
import com.glancy.backend.service.AlertService;
import com.glancy.backend.service.SearchRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchRecordController.class)
class SearchRecordControllerTest {
    @MockBean
    private AlertService alertService;
    
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SearchRecordService searchRecordService;

    @Test
    void testCreate() throws Exception {
        SearchRecordResponse resp = new SearchRecordResponse(1L, 1L, "hello", Language.ENGLISH, LocalDateTime.now());
        when(searchRecordService.saveRecord(any(Long.class), any(SearchRecordRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/search-records/user/1")
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

        mockMvc.perform(get("/api/search-records/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].term").value("hello"));
    }
}
