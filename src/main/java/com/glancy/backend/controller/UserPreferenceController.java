package com.glancy.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.glancy.backend.dto.UserPreferenceRequest;
import com.glancy.backend.dto.UserPreferenceResponse;
import com.glancy.backend.service.UserPreferenceService;

/**
 * Manage user interface and language preferences.
 */
@RestController
@RequestMapping("/api/preferences")
public class UserPreferenceController {
    private final UserPreferenceService userPreferenceService;

    public UserPreferenceController(UserPreferenceService userPreferenceService) {
        this.userPreferenceService = userPreferenceService;
    }

    /**
     * Persist UI and language preferences for a user.
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<UserPreferenceResponse> savePreference(@PathVariable Long userId,
                                                                 @Valid @RequestBody UserPreferenceRequest req) {
        UserPreferenceResponse resp = userPreferenceService.savePreference(userId, req);
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    /**
     * Retrieve preferences previously saved for the user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserPreferenceResponse> getPreference(@PathVariable Long userId) {
        UserPreferenceResponse resp = userPreferenceService.getPreference(userId);
        return ResponseEntity.ok(resp);
    }
}
