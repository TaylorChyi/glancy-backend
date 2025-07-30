package com.glancy.backend.controller;

import com.glancy.backend.entity.LlmModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides meta information about available LLM models.
 */
@RestController
@RequestMapping("/api/llm")
public class LlmController {

    @GetMapping("/models")
    public ResponseEntity<List<String>> getModels() {
        List<String> models = Arrays.stream(LlmModel.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(models);
    }
}
