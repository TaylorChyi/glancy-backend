package com.glancy.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.glancy.backend.dto.ContactRequest;
import com.glancy.backend.dto.ContactResponse;
import com.glancy.backend.service.ContactService;

/**
 * API controller for the "Contact Us" feature. It accepts contact
 * messages from end users and stores them via {@link ContactService}.
 */
@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    /**
     * Submit a contact message. This endpoint supports the requirement for
     * users to reach out to the system administrators.
     */
    @PostMapping
    public ResponseEntity<ContactResponse> submit(@Valid @RequestBody ContactRequest req) {
        ContactResponse resp = contactService.submit(req);
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }
}
