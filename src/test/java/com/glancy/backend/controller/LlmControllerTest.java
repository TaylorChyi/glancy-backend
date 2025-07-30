package com.glancy.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.glancy.backend.service.LlmModelService;
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
    private LlmModelService modelService;

    @Test
    void getModels() throws Exception {
        given(modelService.getModelNames()).willReturn(List.of("DEEPSEEK", "DOUBAO_FLASH"));
        mockMvc.perform(get("/api/llm/models"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("DEEPSEEK"))
                .andExpect(jsonPath("$[1]").value("DOUBAO_FLASH"));
    }
}

