package com.glancy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glancy.backend.dto.UserProfileRequest;
import com.glancy.backend.dto.UserProfileResponse;
import com.glancy.backend.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileController.class)
@Import(com.glancy.backend.config.SecurityConfig.class)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserProfileService userProfileService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试 saveProfile 接口
     */
    @Test
    void saveProfile() throws Exception {
        UserProfileResponse resp = new UserProfileResponse(1L, 2L, 20, "M", "dev", "code", "learn");
        when(userProfileService.saveProfile(eq(2L), any(UserProfileRequest.class))).thenReturn(resp);

        UserProfileRequest req = new UserProfileRequest();
        req.setAge(20);
        req.setGender("M");
        req.setJob("dev");
        req.setInterest("code");
        req.setGoal("learn");

        mockMvc.perform(post("/api/profiles/user/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(2L));
    }

    /**
     * 测试 getProfile 接口
     */
    @Test
    void getProfile() throws Exception {
        UserProfileResponse resp = new UserProfileResponse(1L, 2L, 20, "M", "dev", "code", "learn");
        when(userProfileService.getProfile(2L)).thenReturn(resp);

        mockMvc.perform(get("/api/profiles/user/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(2L));
    }
}
