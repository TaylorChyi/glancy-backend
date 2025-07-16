package com.glancy.backend.service;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import com.glancy.backend.client.DeepSeekClient;
import com.glancy.backend.client.ChatGptClient;
import com.glancy.backend.client.GoogleTtsClient;
import com.glancy.backend.client.GeminiClient;
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
    private final DeepSeekClient deepSeekClient;
    private final ChatGptClient chatGptClient;
    private final GoogleTtsClient googleTtsClient;
    private final GeminiClient geminiClient;
    private final WordRepository wordRepository;

    public WordService(DeepSeekClient deepSeekClient,
                       ChatGptClient chatGptClient,
                       GoogleTtsClient googleTtsClient,
                       GeminiClient geminiClient,
                       WordRepository wordRepository) {
        this.deepSeekClient = deepSeekClient;
        this.chatGptClient = chatGptClient;
        this.googleTtsClient = googleTtsClient;
        this.geminiClient = geminiClient;
        this.wordRepository = wordRepository;
    }

    /**
     * Retrieve word details from the external API.
     */
    @Transactional
    public WordResponse findWordFromDeepSeek(String term, Language language) {
        log.info("Fetching definition for term '{}' in language {}", term, language);
        return wordRepository.findByTermAndLanguageAndDeletedFalse(term, language)
                .map(this::toResponse)
                .orElseGet(() -> {
                    WordResponse resp = deepSeekClient.fetchDefinition(term, language);
                    saveWord(resp);
                    return resp;
                });
    }

    /**
     * Retrieve word details using ChatGPT.
     */
    @Transactional(readOnly = true)
    public WordResponse findWordWithGpt(String term, Language language) {
        log.info("Fetching definition for term '{}' using ChatGPT in language {}", term, language);
        return chatGptClient.fetchDefinition(term, language);
    }

    @Transactional(readOnly = true)
    public byte[] getAudio(String term, Language language) {
        log.info("Fetching audio for term '{}' in language {}", term, language);
        return deepSeekClient.fetchAudio(term, language);


    }

    /**
     * Retrieve pronunciation audio bytes from Google TTS.
     */
    @Transactional(readOnly = true)
    public byte[] getPronunciation(String term, Language language) {
        log.info("Fetching pronunciation for term '{}' in language {}", term, language);
        return googleTtsClient.fetchPronunciation(term, language);
    }
    /**
     * Retrieve word details using the Gemini provider.
     */
    @Transactional(readOnly = true)
    public WordResponse findWordFromGemini(String term, Language language) {
        log.info("Fetching definition from Gemini for term '{}' in language {}", term, language);
        return geminiClient.fetchDefinition(term, language);
    }

    @Transactional(readOnly = true)
    public byte[] getAudioFromDeepSeek(String term, Language language) {
        log.info("Fetching audio for term '{}' in language {}", term, language);
        return deepSeekClient.fetchAudio(term, language);
    }

    private void saveWord(WordResponse resp) {
        Word word = new Word();
        word.setTerm(resp.getTerm());
        word.setLanguage(resp.getLanguage());
        word.setDefinitions(resp.getDefinitions());
        word.setExample(resp.getExample());
        word.setPhonetic(resp.getPhonetic());
        Word saved = wordRepository.save(word);
        resp.setId(saved.getId());
    }

    private WordResponse toResponse(Word word) {
        return new WordResponse(word.getId(), word.getTerm(), word.getDefinitions(),
                word.getLanguage(), word.getExample(), word.getPhonetic());
    }
}
