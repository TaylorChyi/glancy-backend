package com.glancy.backend.service;

import com.glancy.backend.entity.LlmModel;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Provides available LLM model names in a sorted list.
 */
@Service
public class LlmModelService {

    /**
     * Returns all supported model names sorted alphabetically.
     */
    public List<String> getModelNames() {
        return Arrays.stream(LlmModel.values())
                .map(Enum::name)
                .sorted()
                .toList();
    }
}
