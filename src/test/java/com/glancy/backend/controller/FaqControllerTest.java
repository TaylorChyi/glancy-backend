package com.glancy.backend.controller;

import com.glancy.backend.dto.FaqRequest;
import com.glancy.backend.dto.FaqResponse;
import com.glancy.backend.service.FaqService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FaqController.class)
class FaqControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FaqService faqService;

    @Test
    void testCreate() throws Exception {
        FaqResponse resp = new FaqResponse(1L, "Q", "A");
        when(faqService.createFaq(any(FaqRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/faqs")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"question\":\"Q\",\"answer\":\"A\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.question").value("Q"))
                .andExpect(jsonPath("$.answer").value("A"));
    }

    @Test
    void testList() throws Exception {
        when(faqService.getAllFaqs()).thenReturn(Collections.singletonList(new FaqResponse(1L, "Q", "A")));

        mockMvc.perform(get("/api/faqs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].question").value("Q"))
                .andExpect(jsonPath("$[0].answer").value("A"));
    }
}
