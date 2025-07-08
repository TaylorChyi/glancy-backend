package com.glancy.backend.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.glancy.backend.dto.SystemParameterRequest;
import com.glancy.backend.dto.SystemParameterResponse;
import com.glancy.backend.service.SystemParameterService;
import com.glancy.backend.service.UserService;
import com.glancy.backend.dto.UserResponse;

/**
 * Portal endpoints used by administrators to adjust runtime
 * parameters such as system messages.
 */
@RestController
@RequestMapping("/api/portal")
public class PortalController {

    private final SystemParameterService parameterService;
    private final UserService userService;

    public PortalController(SystemParameterService parameterService, UserService userService) {
        this.parameterService = parameterService;
        this.userService = userService;
    }

    /**
     * Create or update a parameter value.
     */
    @PostMapping("/parameters")
    public ResponseEntity<SystemParameterResponse> upsert(
            @Valid @RequestBody SystemParameterRequest req) {
        SystemParameterResponse resp = parameterService.upsert(req);
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    /**
     * Get a single parameter by name.
     */
    @GetMapping("/parameters/{name}")
    public ResponseEntity<SystemParameterResponse> get(@PathVariable String name) {
        SystemParameterResponse resp = parameterService.getByName(name);
        return ResponseEntity.ok(resp);
    }

    /**
     * List all parameters currently stored.
     */
    @GetMapping("/parameters")
    public ResponseEntity<List<SystemParameterResponse>> list() {
        List<SystemParameterResponse> resp = parameterService.list();
        return ResponseEntity.ok(resp);
    }

    /**
     * Enable membership for a user.
     */
    @PostMapping("/users/{userId}/membership")
    public ResponseEntity<UserResponse> enableMembership(@PathVariable Long userId) {
        UserResponse resp = userService.enableMembership(userId);
        return ResponseEntity.ok(resp);
    }
}
