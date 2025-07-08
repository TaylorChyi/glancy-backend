package com.glancy.backend.controller;

import com.glancy.backend.dto.SearchRecordRequest;
import com.glancy.backend.dto.SearchRecordResponse;
import com.glancy.backend.service.SearchRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search-records")
public class SearchRecordController {
    private final SearchRecordService searchRecordService;

    public SearchRecordController(SearchRecordService searchRecordService) {
        this.searchRecordService = searchRecordService;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<SearchRecordResponse> create(@PathVariable Long userId,
                                                       @Valid @RequestBody SearchRecordRequest req) {
        SearchRecordResponse resp = searchRecordService.saveRecord(userId, req);
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SearchRecordResponse>> list(@PathVariable Long userId) {
        List<SearchRecordResponse> resp = searchRecordService.getRecords(userId);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> clear(@PathVariable Long userId) {
        searchRecordService.clearRecords(userId);
        return ResponseEntity.noContent().build();
    }
}
