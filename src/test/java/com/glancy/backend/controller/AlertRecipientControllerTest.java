package com.glancy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glancy.backend.dto.AlertRecipientRequest;
import com.glancy.backend.dto.AlertRecipientResponse;
import com.glancy.backend.service.AlertRecipientService;
import com.glancy.backend.service.AlertService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

@WebMvcTest(AlertRecipientController.class)
@Import(com.glancy.backend.config.SecurityConfig.class)
class AlertRecipientControllerTest {
    @MockBean
    private AlertService alertService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlertRecipientService alertRecipientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createRecipient() throws Exception {
        AlertRecipientResponse resp = new AlertRecipientResponse(1L, "a@example.com");
        when(alertRecipientService.addRecipient(any(AlertRecipientRequest.class))).thenReturn(resp);

        AlertRecipientRequest req = new AlertRecipientRequest();
        req.setEmail("a@example.com");

        mockMvc.perform(post("/api/portal/alert-recipients")
                        .with(httpBasic("admin", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void listRecipients() throws Exception {
        when(alertRecipientService.listRecipients()).thenReturn(List.of(new AlertRecipientResponse(1L, "a@example.com")));

        mockMvc.perform(get("/api/portal/alert-recipients").with(httpBasic("admin", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void updateRecipient() throws Exception {
        AlertRecipientResponse resp = new AlertRecipientResponse(1L, "b@example.com");
        when(alertRecipientService.updateRecipient(eq(1L), any(AlertRecipientRequest.class))).thenReturn(resp);

        AlertRecipientRequest req = new AlertRecipientRequest();
        req.setEmail("b@example.com");

        mockMvc.perform(put("/api/portal/alert-recipients/1")
                        .with(httpBasic("admin", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("b@example.com"));
    }

    @Test
    void deleteRecipient() throws Exception {
        doNothing().when(alertRecipientService).deleteRecipient(1L);
        mockMvc.perform(delete("/api/portal/alert-recipients/1").with(httpBasic("admin", "password")))
                .andExpect(status().isOk());
    }
}
