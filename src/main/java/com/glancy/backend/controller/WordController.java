package com.glancy.backend.controller;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.dto.SearchRecordRequest;
import com.glancy.backend.entity.Language;
import com.glancy.backend.service.WordService;
import com.glancy.backend.service.SearchRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides dictionary lookup functionality. Each request also
 * records the search for history tracking.
 */
@RestController
@RequestMapping("/api/words")
public class WordController {
    private final WordService wordService;
    private final SearchRecordService searchRecordService;

    public WordController(WordService wordService,
                          SearchRecordService searchRecordService) {
        this.wordService = wordService;
        this.searchRecordService = searchRecordService;
    }

    /**
     * Look up a word definition and save the search record.
     */
    @GetMapping
    public ResponseEntity<WordResponse> getWord(@RequestParam Long userId,
                                                @RequestParam String term,
                                                @RequestParam Language language) {
        SearchRecordRequest req = new SearchRecordRequest();
        req.setTerm(term);
        req.setLanguage(language);
        searchRecordService.saveRecord(userId, req);
        WordResponse resp = wordService.findWord(term, language);
        return ResponseEntity.ok(resp);
    }
}
