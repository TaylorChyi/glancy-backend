package com.glancy.backend.controller;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.dto.SearchRecordRequest;
import com.glancy.backend.entity.Language;
import org.springframework.http.MediaType;
import com.glancy.backend.service.WordService;
import com.glancy.backend.service.SearchRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.glancy.backend.config.auth.AuthenticatedUser;
import lombok.extern.slf4j.Slf4j;

/**
 * Provides dictionary lookup functionality. Each request also
 * records the search for history tracking.
 */
@RestController
@RequestMapping("/api/words")
@Slf4j
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
    public ResponseEntity<WordResponse> getWord(@AuthenticatedUser Long userId,
                                                @RequestParam String term,
                                                @RequestParam Language language,
                                                @RequestParam(required = false) String model) {
        log.info("Received getWord request from user {} with term '{}' and language {} using model {}",
                userId, term, language, model);
        SearchRecordRequest req = new SearchRecordRequest();
        req.setTerm(term);
        req.setLanguage(language);
        searchRecordService.saveRecord(userId, req);
        WordResponse resp = wordService.findWordForUser(userId, term, language, model);
        log.info("Returning word response for term '{}': {}", term, resp);
        return ResponseEntity.ok(resp);
    }

    /**
     * Retrieve the pronunciation audio for a word.
     */
    @GetMapping(value = "/audio", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getAudio(@RequestParam String term,
                                           @RequestParam Language language) {
        byte[] data = wordService.getAudio(term, language);
        return ResponseEntity.ok(data);
    }


}
