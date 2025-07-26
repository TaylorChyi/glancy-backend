package com.glancy.backend.service;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import com.glancy.backend.client.DictionaryClient;
import com.glancy.backend.entity.DictionaryModel;
import com.glancy.backend.repository.UserPreferenceRepository;
import com.glancy.backend.entity.UserPreference;
import com.glancy.backend.llm.service.WordSearcher;
import org.springframework.beans.factory.annotation.Qualifier;
import com.glancy.backend.entity.Word;
import com.glancy.backend.repository.WordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Performs dictionary lookups via the configured third-party client.
 */
@Slf4j
@Service
public class WordService {
    private final DictionaryClient dictionaryClient;
    private final WordSearcher wordSearcher;
    private final WordRepository wordRepository;
    private final UserPreferenceRepository userPreferenceRepository;

    public WordService(@Qualifier("deepSeekClient") DictionaryClient dictionaryClient,
                       WordSearcher wordSearcher,
                       WordRepository wordRepository,
                       UserPreferenceRepository userPreferenceRepository) {
        this.dictionaryClient = dictionaryClient;
        this.wordSearcher = wordSearcher;
        this.wordRepository = wordRepository;
        this.userPreferenceRepository = userPreferenceRepository;
    }

    /**
     * Retrieve pronunciation audio from the DeepSeek service.
     */
    @Transactional(readOnly = true)
    public byte[] getAudio(String term, Language language) {
        log.info("Fetching audio for term '{}' in language {}", term, language);
        return dictionaryClient.fetchAudio(term, language);
    }

    @Transactional
    public WordResponse findWordForUser(Long userId, String term, Language language) {
        var pref = userPreferenceRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserPreference p = new UserPreference();
                    p.setDictionaryModel(DictionaryModel.DEEPSEEK);
                    return p;
                });
        DictionaryModel model = pref.getDictionaryModel();
        String clientName = model.name().toLowerCase();
        if (model == DictionaryModel.DEEPSEEK) {
            return wordRepository.findByTermAndLanguageAndDeletedFalse(term, language)
                    .map(this::toResponse)
                    .orElseGet(() -> {
                        WordResponse resp = wordSearcher.search(term, language, clientName);
                        saveWord(term, resp, language);
                        return resp;
                    });
        }
        return wordSearcher.search(term, language, clientName);
    }

    private void saveWord(String requestedTerm, WordResponse resp, Language language) {
        Word word = new Word();
        String term = resp.getTerm() != null ? resp.getTerm() : requestedTerm;
        word.setTerm(term);
        Language lang = resp.getLanguage() != null ? resp.getLanguage() : language;
        word.setLanguage(lang);
        word.setDefinitions(resp.getDefinitions());
        word.setExample(resp.getExample());
        word.setPhonetic(resp.getPhonetic());
        Word saved = wordRepository.save(word);
        resp.setId(String.valueOf(saved.getId()));
        resp.setLanguage(lang);
        resp.setTerm(term);
    }

    private WordResponse toResponse(Word word) {
        return new WordResponse(String.valueOf(word.getId()), word.getTerm(), word.getDefinitions(),
                word.getLanguage(), word.getExample(), word.getPhonetic());
    }
}
