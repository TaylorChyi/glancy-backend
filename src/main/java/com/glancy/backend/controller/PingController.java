package com.glancy.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

/**
 * Simple endpoint used by monitoring tools to verify the service is running.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class PingController {

    /**
     * Respond with "pong" when the service is healthy.
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        log.info("Received ping request");
        return ResponseEntity.ok("pong");
    }
}
