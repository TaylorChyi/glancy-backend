package com.glancy.backend.controller;

import com.glancy.backend.llm.llm.LLMClientFactory;
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

    public LlmController(LLMClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @GetMapping("/models")
    public ResponseEntity<List<String>> getModels() {
        List<String> models = clientFactory.getClientNames();
        return ResponseEntity.ok(models);
    }
}
