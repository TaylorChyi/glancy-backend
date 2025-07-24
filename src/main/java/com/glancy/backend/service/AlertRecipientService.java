package com.glancy.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glancy.backend.dto.AlertRecipientRequest;
import com.glancy.backend.dto.AlertRecipientResponse;
import com.glancy.backend.entity.AlertRecipient;
import com.glancy.backend.repository.AlertRecipientRepository;
import com.glancy.backend.exception.ResourceNotFoundException;
import com.glancy.backend.exception.DuplicateResourceException;

/**
 * Business logic for managing alert recipient email addresses.
 */
@Service
public class AlertRecipientService {

    private final AlertRecipientRepository repository;

    public AlertRecipientService(AlertRecipientRepository repository) {
        this.repository = repository;
    }

    /**
     * Add a new email address to the alert recipient list.
     */
    @Transactional
    public AlertRecipientResponse addRecipient(AlertRecipientRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            AlertRecipient existing = repository.findAll().stream()
                    .filter(r -> r.getEmail().equals(request.getEmail()))
                    .findFirst().orElse(null);
            return toResponse(existing);
        }
        AlertRecipient recipient = new AlertRecipient();
        recipient.setEmail(request.getEmail());
        AlertRecipient saved = repository.save(recipient);
        return toResponse(saved);
    }

    /**
     * Update an existing email address.
     */
    @Transactional
    public AlertRecipientResponse updateRecipient(Long id, AlertRecipientRequest request) {
        AlertRecipient recipient = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("收件人不存在"));
        if (!recipient.getEmail().equals(request.getEmail()) && repository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("邮箱已存在");
        }
        recipient.setEmail(request.getEmail());
        AlertRecipient saved = repository.save(recipient);
        return toResponse(saved);
    }

    /**
     * Remove an email address by id.
     */
    @Transactional
    public void deleteRecipient(Long id) {
        repository.deleteById(id);
    }

    /**
     * List all stored email addresses.
     */
    @Transactional(readOnly = true)
    public List<AlertRecipientResponse> listRecipients() {
        return repository.findAll().stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    private AlertRecipientResponse toResponse(AlertRecipient recipient) {
        return new AlertRecipientResponse(recipient.getId(), recipient.getEmail());
    }
}
