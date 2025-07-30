package com.glancy.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.glancy.backend.llm.llm.LLMClientFactory;
import java.util.List;
import static org.mockito.BDDMockito.given;

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

    @MockitoBean
    private LLMClientFactory clientFactory;

    @Test
    void getModels() throws Exception {
        given(clientFactory.getClientNames()).willReturn(List.of("deepseek", "doubao"));
        mockMvc.perform(get("/api/llm/models"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("deepseek"))
                .andExpect(jsonPath("$[1]").value("doubao"));
    }
}

