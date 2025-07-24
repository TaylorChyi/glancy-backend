package com.glancy.backend.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.glancy.backend.dto.SystemParameterRequest;
import com.glancy.backend.dto.SystemParameterResponse;
import com.glancy.backend.dto.UserStatisticsResponse;
import com.glancy.backend.dto.DailyActiveUserResponse;
import com.glancy.backend.service.UserService;
import com.glancy.backend.dto.LogLevelRequest;
import com.glancy.backend.service.SystemParameterService;
import com.glancy.backend.service.LoggingService;

/**
 * Portal endpoints used by administrators to adjust runtime
 * parameters such as system messages.
 */
@RestController
@RequestMapping("/api/portal")
public class PortalController {

    private final SystemParameterService parameterService;
    private final UserService userService;
    private final LoggingService loggingService;
    private static final String EMAIL_PARAM = "email.notifications.enabled";

    public PortalController(
            SystemParameterService parameterService,
            UserService userService,
            LoggingService loggingService) {
        this.parameterService = parameterService;
        this.userService = userService;
        this.loggingService = loggingService;
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
     * Provide aggregated user statistics.
     */
    @GetMapping("/user-stats")
    public ResponseEntity<UserStatisticsResponse> userStats() {
        UserStatisticsResponse resp = userService.getStatistics();
        return ResponseEntity.ok(resp);
    }

    /**
     * Get today's active user stats.
     */
    @GetMapping("/daily-active")
    public ResponseEntity<DailyActiveUserResponse> dailyActive() {
        DailyActiveUserResponse resp = userService.getDailyActiveStats();
        return ResponseEntity.ok(resp);
    }

    /**
     * Activate membership for a user.
     */
    @PostMapping("/users/{id}/member")
    public ResponseEntity<Void> activateMember(@PathVariable Long id) {
        userService.activateMembership(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Remove membership from a user.
     */
    @DeleteMapping("/users/{id}/member")
    public ResponseEntity<Void> removeMember(@PathVariable Long id) {
        userService.removeMembership(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Change the log level for a given logger.
     */
    @PostMapping("/log-level")
    public ResponseEntity<Void> setLogLevel(
            @RequestHeader("X-ADMIN-TOKEN") String token,
            @Valid @RequestBody LogLevelRequest req) {
        if (!loggingService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        loggingService.setLogLevel(req.getLogger(), req.getLevel());
        return ResponseEntity.ok().build();
    }

    /**
     * Enable or disable alert emails globally.
     */
    @PostMapping("/email-enabled")
    public ResponseEntity<Void> setEmailEnabled(@RequestParam boolean enabled) {
        SystemParameterRequest req = new SystemParameterRequest();
        req.setName(EMAIL_PARAM);
        req.setValue(Boolean.toString(enabled));
        parameterService.upsert(req);
        return ResponseEntity.ok().build();
    }

    /**
     * Check if alert emails are enabled. Defaults to false.
     */
    @GetMapping("/email-enabled")
    public ResponseEntity<Boolean> isEmailEnabled() {
        SystemParameterResponse resp = parameterService.getByName(EMAIL_PARAM);
        return ResponseEntity.ok(Boolean.parseBoolean(resp.getValue()));
    }
}
