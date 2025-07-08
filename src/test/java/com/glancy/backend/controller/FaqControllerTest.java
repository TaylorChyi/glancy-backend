package com.glancy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glancy.backend.dto.FaqRequest;
import com.glancy.backend.dto.FaqResponse;
import com.glancy.backend.service.FaqService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FaqController.class)
class FaqControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FaqService faqService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createFaq() throws Exception {
        FaqResponse resp = new FaqResponse(1L, "Q", "A");
        when(faqService.createFaq(any(FaqRequest.class))).thenReturn(resp);

        FaqRequest req = new FaqRequest();
        req.setQuestion("Q");
        req.setAnswer("A");

        mockMvc.perform(post("/api/faqs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.question").value("Q"))
                .andExpect(jsonPath("$.answer").value("A"));
    }

    @Test
    void listFaqs() throws Exception {
        FaqResponse resp = new FaqResponse(1L, "Q", "A");
        when(faqService.getAllFaqs()).thenReturn(List.of(resp));

        mockMvc.perform(get("/api/faqs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}
