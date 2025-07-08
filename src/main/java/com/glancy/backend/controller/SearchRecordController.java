package com.glancy.backend.controller;

import com.glancy.backend.dto.SearchRecordRequest;
import com.glancy.backend.dto.SearchRecordResponse;
import com.glancy.backend.service.SearchRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints for managing user search history. It allows recording
 * each search and provides retrieval and clearing operations.
 */
@RestController
@RequestMapping("/api/search-records")
public class SearchRecordController {
    private final SearchRecordService searchRecordService;

    public SearchRecordController(SearchRecordService searchRecordService) {
        this.searchRecordService = searchRecordService;
    }

    /**
     * Record a search term for a user. Non-members are limited to
     * 10 searches per day as enforced in the service layer.
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<SearchRecordResponse> create(@PathVariable Long userId,
                                                       @Valid @RequestBody SearchRecordRequest req) {
        SearchRecordResponse resp = searchRecordService.saveRecord(userId, req);
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    /**
     * Get a user's search history ordered by latest first.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SearchRecordResponse>> list(@PathVariable Long userId) {
        List<SearchRecordResponse> resp = searchRecordService.getRecords(userId);
        return ResponseEntity.ok(resp);
    }

    /**
     * Clear all search records for a user.
     */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> clear(@PathVariable Long userId) {
        searchRecordService.clearRecords(userId);
        return ResponseEntity.noContent().build();
    }
}
