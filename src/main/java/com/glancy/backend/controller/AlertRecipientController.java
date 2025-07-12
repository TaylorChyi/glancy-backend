package com.glancy.backend.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.glancy.backend.dto.AlertRecipientRequest;
import com.glancy.backend.dto.AlertRecipientResponse;
import com.glancy.backend.service.AlertRecipientService;

/**
 * Portal endpoints for managing alert recipient email addresses.
 */
@RestController
@RequestMapping("/api/portal/alert-recipients")
public class AlertRecipientController {

    private final AlertRecipientService alertRecipientService;

    public AlertRecipientController(AlertRecipientService alertRecipientService) {
        this.alertRecipientService = alertRecipientService;
    }

    /**
     * Add a new alert recipient email address.
     */
    @PostMapping
    public ResponseEntity<AlertRecipientResponse> create(@Valid @RequestBody AlertRecipientRequest req) {
        AlertRecipientResponse resp = alertRecipientService.addRecipient(req);
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    /**
     * List all alert recipient email addresses.
     */
    @GetMapping
    public ResponseEntity<List<AlertRecipientResponse>> list() {
        List<AlertRecipientResponse> resp = alertRecipientService.listRecipients();
        return ResponseEntity.ok(resp);
    }

    /**
     * Remove an alert recipient by id.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        alertRecipientService.deleteRecipient(id);
        return ResponseEntity.ok().build();
    }
}
