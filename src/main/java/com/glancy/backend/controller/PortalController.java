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
     * Change the log level for a given logger.
     */
    @PostMapping("/log-level")
    public ResponseEntity<Void> setLogLevel(
            @Valid @RequestBody LogLevelRequest req) {
        loggingService.setLogLevel(req.getLogger(), req.getLevel());
        return ResponseEntity.ok().build();
    }
}
