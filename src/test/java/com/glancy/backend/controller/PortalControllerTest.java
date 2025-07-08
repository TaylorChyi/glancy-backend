package com.glancy.backend.controller;

import com.glancy.backend.dto.UserResponse;
import com.glancy.backend.service.SystemParameterService;
import com.glancy.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

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


    @Test
    void enableMembership() throws Exception {
        UserResponse resp = new UserResponse(1L, "u", "e", null, null);
        when(userService.enableMembership(1L)).thenReturn(resp);

        mockMvc.perform(post("/api/portal/users/1/membership"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
}
