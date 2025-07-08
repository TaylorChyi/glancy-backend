package com.glancy.backend.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.glancy.backend.dto.FaqRequest;
import com.glancy.backend.dto.FaqResponse;
import com.glancy.backend.service.FaqService;

@RestController
@RequestMapping("/api/faqs")
public class FaqController {

    private final FaqService faqService;

    public FaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    @PostMapping
    public ResponseEntity<FaqResponse> create(@Valid @RequestBody FaqRequest req) {
        FaqResponse resp = faqService.createFaq(req);
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FaqResponse>> list() {
        List<FaqResponse> resp = faqService.getAllFaqs();
        return ResponseEntity.ok(resp);
    }
}
