package com.glancy.backend.service;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import com.glancy.backend.entity.Word;
import com.glancy.backend.repository.WordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WordService {
    private final WordRepository wordRepository;

    public WordService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    @Transactional(readOnly = true)
    public WordResponse findWord(String term, Language language) {
        Word word = wordRepository.findByTermAndLanguageAndDeletedFalse(term, language)
                .orElseThrow(() -> new IllegalArgumentException("单词不存在"));
        return toResponse(word);
    }

    private WordResponse toResponse(Word word) {
        return new WordResponse(word.getId(), word.getTerm(), word.getDefinitions(),
                word.getLanguage(), word.getExample());
    }
}
