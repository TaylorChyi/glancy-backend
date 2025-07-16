package com.glancy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glancy.backend.dto.UserStatisticsResponse;
import com.glancy.backend.dto.DailyActiveUserResponse;
import com.glancy.backend.dto.SystemParameterRequest;
import com.glancy.backend.dto.SystemParameterResponse;
import com.glancy.backend.dto.LogLevelRequest;
import com.glancy.backend.service.UserService;
import com.glancy.backend.service.AlertService;
import com.glancy.backend.service.LoggingService;
import com.glancy.backend.service.SystemParameterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

@WebMvcTest(PortalController.class)
@Import(com.glancy.backend.config.SecurityConfig.class)
class PortalControllerTest {
    @MockBean
    private AlertService alertService;
    
    @MockBean
    private LoggingService loggingService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SystemParameterService parameterService;
    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void userStats() throws Exception {
        UserStatisticsResponse resp = new UserStatisticsResponse(2, 1, 0);
        when(userService.getStatistics()).thenReturn(resp);
        mockMvc.perform(get("/api/portal/user-stats").with(httpBasic("admin", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(2));
    }

    @Test
    void dailyActive() throws Exception {
        DailyActiveUserResponse resp = new DailyActiveUserResponse(1, 0.5);
        when(userService.getDailyActiveStats()).thenReturn(resp);
        mockMvc.perform(get("/api/portal/daily-active").with(httpBasic("admin", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeUsers").value(1));
    }

    @Test
    void upsertParameter() throws Exception {
        SystemParameterResponse resp = new SystemParameterResponse(1L, "n", "v");
        when(parameterService.upsert(any(SystemParameterRequest.class))).thenReturn(resp);
        SystemParameterRequest req = new SystemParameterRequest();
        req.setName("n");
        req.setValue("v");
        mockMvc.perform(post("/api/portal/parameters")
                        .with(httpBasic("admin", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void activateMember() throws Exception {
        doNothing().when(userService).activateMembership(1L);
        mockMvc.perform(post("/api/portal/users/1/member").with(httpBasic("admin", "password")))
                .andExpect(status().isOk());
    }

    @Test
    void removeMember() throws Exception {
        doNothing().when(userService).removeMembership(1L);
        mockMvc.perform(delete("/api/portal/users/1/member").with(httpBasic("admin", "password")))
                .andExpect(status().isOk());
    }

    @Test
    void setEmailEnabled() throws Exception {
        SystemParameterResponse resp = new SystemParameterResponse(1L,
                "email.notifications.enabled", "true");
        when(parameterService.upsert(any(SystemParameterRequest.class)))
                .thenReturn(resp);
        mockMvc.perform(post("/api/portal/email-enabled?enabled=true").with(httpBasic("admin", "password")))
                .andExpect(status().isOk());
    }

    @Test
    void getEmailEnabled() throws Exception {
        SystemParameterResponse resp = new SystemParameterResponse(1L,
                "email.notifications.enabled", "true");
        when(parameterService.getByName("email.notifications.enabled"))
                .thenReturn(resp);
        mockMvc.perform(get("/api/portal/email-enabled").with(httpBasic("admin", "password")))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void setLogLevelAuthorized() throws Exception {
        LogLevelRequest req = new LogLevelRequest();
        req.setLogger("com.test");
        req.setLevel("INFO");
        when(loggingService.isTokenValid("secret")).thenReturn(true);
        doNothing().when(loggingService).setLogLevel("com.test", "INFO");

        mockMvc.perform(post("/api/portal/log-level")
                        .header("X-ADMIN-TOKEN", "secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void setLogLevelUnauthorized() throws Exception {
        LogLevelRequest req = new LogLevelRequest();
        req.setLogger("com.test");
        req.setLevel("INFO");
        when(loggingService.isTokenValid("bad")).thenReturn(false);

        mockMvc.perform(post("/api/portal/log-level")
                        .header("X-ADMIN-TOKEN", "bad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void setLogLevelInvalid() throws Exception {
        LogLevelRequest req = new LogLevelRequest();
        req.setLogger("com.test");
        req.setLevel("NOPE");
        when(loggingService.isTokenValid("secret")).thenReturn(true);
        doThrow(new IllegalArgumentException("Invalid log level"))
                .when(loggingService).setLogLevel("com.test", "NOPE");

        mockMvc.perform(post("/api/portal/log-level")
                        .header("X-ADMIN-TOKEN", "secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}
