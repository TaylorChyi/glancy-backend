package com.glancy.backend.controller;

import com.glancy.backend.dto.TrafficRecordRequest;
import com.glancy.backend.dto.TrafficRecordResponse;
import com.glancy.backend.service.TrafficRecordService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Endpoints for portal traffic monitoring.
 */
@RestController
@RequestMapping("/api/portal/traffic")
public class PortalTrafficController {

    private final TrafficRecordService trafficRecordService;

    public PortalTrafficController(TrafficRecordService trafficRecordService) {
        this.trafficRecordService = trafficRecordService;
    }

    /**
     * Record a portal visit.
     */
    @PostMapping
    public ResponseEntity<TrafficRecordResponse> record(@Valid @RequestBody TrafficRecordRequest req) {
        TrafficRecordResponse resp = trafficRecordService.record(req);
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    /**
     * Get daily visit counts between two dates.
     */
    @GetMapping("/daily")
    public ResponseEntity<List<Long>> daily(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        List<Long> counts = trafficRecordService.countDaily(start, end);
        return ResponseEntity.ok(counts);
    }
}
