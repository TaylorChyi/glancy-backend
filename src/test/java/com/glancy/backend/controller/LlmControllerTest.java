package com.glancy.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(LlmController.class)
@Import({com.glancy.backend.config.SecurityConfig.class,
        com.glancy.backend.config.WebConfig.class,
        com.glancy.backend.config.TokenAuthenticationInterceptor.class,
        com.glancy.backend.config.auth.AuthenticatedUserArgumentResolver.class})
class LlmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getModels() throws Exception {
        mockMvc.perform(get("/api/llm/models"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("DEEPSEEK"));
    }
}

