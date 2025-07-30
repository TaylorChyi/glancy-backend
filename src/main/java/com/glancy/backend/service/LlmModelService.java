package com.glancy.backend.service;

import com.glancy.backend.entity.LlmModel;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * Provides available LLM model names in a sorted list.
 */
@Slf4j
@Service
public class LlmModelService {

    /**
     * Returns all supported model names sorted alphabetically.
     */
    public List<String> getModelNames() {
        log.debug("Fetching supported LLM model names");
        List<String> names = Arrays.stream(LlmModel.values())
                .map(Enum::name)
                .sorted()
                .toList();
        log.debug("Available models: {}", names);
        return names;
    }
}
