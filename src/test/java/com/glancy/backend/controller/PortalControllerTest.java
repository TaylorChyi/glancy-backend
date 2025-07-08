package com.glancy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glancy.backend.dto.MemberStatusResponse;
import com.glancy.backend.service.SystemParameterService;
import com.glancy.backend.service.UserService;
import com.glancy.backend.dto.UserStatisticsResponse;
import com.glancy.backend.dto.SystemParameterRequest;
import com.glancy.backend.dto.SystemParameterResponse;
import com.glancy.backend.service.UserService;
import com.glancy.backend.service.SystemParameterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PortalController.class)
class PortalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SystemParameterService parameterService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getMembershipStatus() throws Exception {
        when(userService.isMember(1L)).thenReturn(true);

        mockMvc.perform(get("/api/portal/users/1/membership")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.member").value(true))
                .andExpect(jsonPath("$.userId").value(1L));
    }
  
    @Test
    void userStats() throws Exception {
        UserStatisticsResponse resp = new UserStatisticsResponse(2, 1, 0);
        when(userService.getStatistics()).thenReturn(resp);
        mockMvc.perform(get("/api/portal/user-stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(2));
    }

    @Test
    void upsertParameter() throws Exception {
        SystemParameterResponse resp = new SystemParameterResponse(1L, "n", "v");
        when(parameterService.upsert(any(SystemParameterRequest.class))).thenReturn(resp);
        SystemParameterRequest req = new SystemParameterRequest();
        req.setName("n");
        req.setValue("v");
        mockMvc.perform(post("/api/portal/parameters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }
}
