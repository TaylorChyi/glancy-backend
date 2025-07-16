package com.glancy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glancy.backend.dto.UserPreferenceRequest;
import com.glancy.backend.dto.UserPreferenceResponse;
import com.glancy.backend.service.AlertService;
import com.glancy.backend.service.UserPreferenceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserPreferenceController.class)
@Import(com.glancy.backend.config.SecurityConfig.class)
class UserPreferenceControllerTest {
    @MockBean
    private AlertService alertService;
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserPreferenceService userPreferenceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void savePreference() throws Exception {
        UserPreferenceResponse resp = new UserPreferenceResponse(1L, 2L, "dark", "en", "en");
        when(userPreferenceService.savePreference(eq(2L), any(UserPreferenceRequest.class))).thenReturn(resp);

        UserPreferenceRequest req = new UserPreferenceRequest();
        req.setTheme("dark");
        req.setSystemLanguage("en");
        req.setSearchLanguage("en");

        mockMvc.perform(post("/api/preferences/user/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(2L));
    }

    @Test
    void getPreference() throws Exception {
        UserPreferenceResponse resp = new UserPreferenceResponse(1L, 2L, "dark", "en", "en");
        when(userPreferenceService.getPreference(2L)).thenReturn(resp);

        mockMvc.perform(get("/api/preferences/user/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(2L));
    }
}
