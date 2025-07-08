package com.glancy.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glancy.backend.dto.*;
import com.glancy.backend.entity.User;
import com.glancy.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register() throws Exception {
        UserResponse resp = new UserResponse(1L, "u", "e", null, null);
        when(userService.register(any(UserRegistrationRequest.class))).thenReturn(resp);

        UserRegistrationRequest req = new UserRegistrationRequest();
        req.setUsername("u");
        req.setPassword("pass123");
        req.setEmail("e");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void deleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("u");
        user.setPassword("p");
        user.setEmail("e");
        when(userService.getUserRaw(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void login() throws Exception {
        LoginResponse resp = new LoginResponse(1L, "u", "e", null, null);
        when(userService.login(any(LoginRequest.class))).thenReturn(resp);

        LoginRequest req = new LoginRequest();
        req.setUsername("u");
        req.setPassword("pass");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void bindThirdParty() throws Exception {
        doNothing().when(userService).bindThirdPartyAccount(eq(1L), any(ThirdPartyAccountRequest.class));

        ThirdPartyAccountRequest req = new ThirdPartyAccountRequest();
        req.setProvider("p");
        req.setExternalId("e");

        mockMvc.perform(post("/api/users/1/third-party-accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }}
