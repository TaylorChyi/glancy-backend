package com.glancy.backend.controller;

import com.glancy.backend.llm.llm.LLMClientFactory;
import com.glancy.backend.service.LlmModelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Provides meta information about available LLM models.
 */
@RestController
@RequestMapping("/api/llm")
public class LlmController {
    private final LLMClientFactory clientFactory;
    private final LlmModelService modelService;

    public LlmController(LLMClientFactory clientFactory, LlmModelService modelService) {
        this.clientFactory = clientFactory;
        this.modelService = modelService;
    }

    @GetMapping("/models")
    public ResponseEntity<List<String>> getModels() {
        List<String> models = modelService.getModelNames();
        return ResponseEntity.ok(models);
    }
}
