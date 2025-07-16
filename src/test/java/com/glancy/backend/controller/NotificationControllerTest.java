package com.glancy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glancy.backend.dto.NotificationRequest;
import com.glancy.backend.dto.NotificationResponse;
import com.glancy.backend.service.AlertService;
import com.glancy.backend.service.NotificationService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

@WebMvcTest(NotificationController.class)
@Import(com.glancy.backend.config.SecurityConfig.class)
class NotificationControllerTest {
    @MockBean
    private AlertService alertService;
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createSystemNotification() throws Exception {
        NotificationResponse resp = new NotificationResponse(1L, "msg", true, null);
        when(notificationService.createSystemNotification(any(NotificationRequest.class))).thenReturn(resp);

        NotificationRequest req = new NotificationRequest();
        req.setMessage("msg");

        mockMvc.perform(post("/api/notifications/system")
                        .with(httpBasic("admin", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.systemLevel").value(true));
    }

    @Test
    void createUserNotification() throws Exception {
        NotificationResponse resp = new NotificationResponse(1L, "msg", false, 2L);
        when(notificationService.createUserNotification(eq(2L), any(NotificationRequest.class))).thenReturn(resp);

        NotificationRequest req = new NotificationRequest();
        req.setMessage("msg");

        mockMvc.perform(post("/api/notifications/user/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(2L));
    }

    @Test
    void getNotificationsForUser() throws Exception {
        NotificationResponse uresp = new NotificationResponse(1L, "user", false, 2L);
        NotificationResponse sresp = new NotificationResponse(2L, "sys", true, null);
        when(notificationService.getNotificationsForUser(2L)).thenReturn(List.of(uresp, sresp));

        mockMvc.perform(get("/api/notifications/user/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("user"))
                .andExpect(jsonPath("$[1].message").value("sys"));
    }
}
