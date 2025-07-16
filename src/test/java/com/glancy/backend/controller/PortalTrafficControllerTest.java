package com.glancy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glancy.backend.dto.TrafficRecordRequest;
import com.glancy.backend.dto.TrafficRecordResponse;
import com.glancy.backend.service.AlertService;
import com.glancy.backend.service.TrafficRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

@WebMvcTest(PortalTrafficController.class)
@Import(com.glancy.backend.config.SecurityConfig.class)
class PortalTrafficControllerTest {
    @MockBean
    private AlertService alertService;
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrafficRecordService trafficRecordService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void recordTraffic() throws Exception {
        TrafficRecordResponse resp = new TrafficRecordResponse(1L, "/", "ip",
                "ua", LocalDateTime.now());
        when(trafficRecordService.record(any(TrafficRecordRequest.class)))
                .thenReturn(resp);

        TrafficRecordRequest req = new TrafficRecordRequest();
        req.setPath("/");
        req.setIp("ip");
        req.setUserAgent("ua");

        mockMvc.perform(post("/api/portal/traffic")
                        .with(httpBasic("admin", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void dailyCounts() throws Exception {
        when(trafficRecordService.countDaily(LocalDate.parse("2024-01-01"),
                LocalDate.parse("2024-01-02")))
                .thenReturn(List.of(5L, 3L));

        mockMvc.perform(get("/api/portal/traffic/daily")
                        .with(httpBasic("admin", "password"))
                        .param("start", "2024-01-01")
                        .param("end", "2024-01-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(5))
                .andExpect(jsonPath("$[1]").value(3));
    }
}
