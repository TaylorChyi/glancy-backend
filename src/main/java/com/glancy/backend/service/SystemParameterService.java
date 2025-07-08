package com.glancy.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glancy.backend.dto.SystemParameterRequest;
import com.glancy.backend.dto.SystemParameterResponse;
import com.glancy.backend.entity.SystemParameter;
import com.glancy.backend.repository.SystemParameterRepository;

/**
 * Handles creation and retrieval of system parameters that can be
 * changed while the application is running.
 */
@Service
public class SystemParameterService {
    private final SystemParameterRepository parameterRepository;

    public SystemParameterService(SystemParameterRepository parameterRepository) {
        this.parameterRepository = parameterRepository;
    }

    /**
     * Create or update a parameter. Parameters are addressed by name
     * and overwritten if they already exist.
     */
    @Transactional
    public SystemParameterResponse upsert(SystemParameterRequest request) {
        SystemParameter param = parameterRepository.findByName(request.getName())
                .orElse(new SystemParameter());
        param.setName(request.getName());
        param.setValue(request.getValue());
        SystemParameter saved = parameterRepository.save(param);
        return toResponse(saved);
    }

    /**
     * Return the value of a single parameter by name.
     */
    @Transactional(readOnly = true)
    public SystemParameterResponse getByName(String name) {
        SystemParameter param = parameterRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("参数不存在"));
        return toResponse(param);
    }

    /**
     * List all stored parameters.
     */
    @Transactional(readOnly = true)
    public List<SystemParameterResponse> list() {
        return parameterRepository.findAll().stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    private SystemParameterResponse toResponse(SystemParameter param) {
        return new SystemParameterResponse(param.getId(), param.getName(),
                param.getValue());
    }
}
