package com.glancy.backend.controller;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.dto.SearchRecordRequest;
import com.glancy.backend.entity.Language;
import org.springframework.http.MediaType;
import com.glancy.backend.service.WordService;
import com.glancy.backend.service.SearchRecordService;
import com.glancy.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
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
    private final UserService userService;

    public WordController(WordService wordService,
                          SearchRecordService searchRecordService,
                          UserService userService) {
        this.wordService = wordService;
        this.searchRecordService = searchRecordService;
        this.userService = userService;
    }

    /**
     * Look up a word definition and save the search record.
     */
    @GetMapping
    public ResponseEntity<WordResponse> getWord(@RequestParam Long userId,
                                                @RequestHeader("X-USER-TOKEN") String token,
                                                @RequestParam String term,
                                                @RequestParam Language language) {
        userService.validateToken(userId, token);
        SearchRecordRequest req = new SearchRecordRequest();
        req.setTerm(term);
        req.setLanguage(language);
        searchRecordService.saveRecord(userId, req);
        WordResponse resp = wordService.findWordForUser(userId, term, language);
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
