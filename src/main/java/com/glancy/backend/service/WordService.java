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
    public WordResponse findWordForUser(Long userId, String term, Language language, String model) {
        log.info("Finding word '{}' for user {} in language {} using model {}", term, userId, language, model);
        userPreferenceRepository.findByUserId(userId)
                .orElseGet(() -> {
                    log.info("No user preference found for user {}, using default", userId);
                    UserPreference p = new UserPreference();
                    p.setDictionaryModel(DictionaryModel.DEEPSEEK);
                    return p;
                });
        return wordRepository.findByTermAndLanguageAndDeletedFalse(term, language)
                .map(word -> {
                    log.info("Found word '{}' in local repository", term);
                    return toResponse(word);
                })
                .orElseGet(() -> {
                    log.info("Word '{}' not found locally, searching via LLM", term);
                    WordResponse resp = wordSearcher.search(term, language, model);
                    log.info("LLM search result: {}", resp);
                    saveWord(term, resp, language);
                    return resp;
                });
    }

    private void saveWord(String requestedTerm, WordResponse resp, Language language) {
        Word word = new Word();
        String term = resp.getTerm() != null ? resp.getTerm() : requestedTerm;
        word.setTerm(term);
        Language lang = resp.getLanguage() != null ? resp.getLanguage() : language;
        word.setLanguage(lang);
        word.setDefinitions(resp.getDefinitions());
        word.setVariations(resp.getVariations());
        word.setSynonyms(resp.getSynonyms());
        word.setAntonyms(resp.getAntonyms());
        word.setRelated(resp.getRelated());
        word.setPhrases(resp.getPhrases());
        word.setExample(resp.getExample());
        word.setPhonetic(resp.getPhonetic());
        log.info("Persisting new word '{}' with language {}", term, lang);
        Word saved = wordRepository.save(word);
        resp.setId(String.valueOf(saved.getId()));
        resp.setLanguage(lang);
        resp.setTerm(term);
    }

    private WordResponse toResponse(Word word) {
        return new WordResponse(String.valueOf(word.getId()),
                word.getTerm(),
                word.getDefinitions(),
                word.getLanguage(),
                word.getExample(),
                word.getPhonetic(),
                word.getVariations(),
                word.getSynonyms(),
                word.getAntonyms(),
                word.getRelated(),
                word.getPhrases());
    }
}
