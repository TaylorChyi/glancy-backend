package com.glancy.backend.controller;

import com.glancy.backend.service.SystemParameterService;
import com.glancy.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PortalController.class)
class PortalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SystemParameterService systemParameterService;

    @MockBean
    private UserService userService;

    @Test
    void removeMembership() throws Exception {
        doNothing().when(userService).removeMembership(1L);
        mockMvc.perform(delete("/api/portal/users/1/membership"))
                .andExpect(status().isNoContent());
    }
}
