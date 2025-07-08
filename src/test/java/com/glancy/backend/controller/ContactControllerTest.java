package com.glancy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glancy.backend.dto.ContactRequest;
import com.glancy.backend.dto.ContactResponse;
import com.glancy.backend.service.ContactService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactController.class)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactService contactService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void submitContact() throws Exception {
        ContactResponse resp = new ContactResponse(1L, "n", "e", "m");
        when(contactService.submit(any(ContactRequest.class))).thenReturn(resp);

        ContactRequest req = new ContactRequest();
        req.setName("n");
        req.setEmail("e");
        req.setMessage("m");

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }
}
