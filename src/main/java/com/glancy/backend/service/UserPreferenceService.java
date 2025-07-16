package com.glancy.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import com.glancy.backend.dto.UserPreferenceRequest;
import com.glancy.backend.dto.UserPreferenceResponse;
import com.glancy.backend.entity.User;
import com.glancy.backend.entity.UserPreference;
import com.glancy.backend.repository.UserPreferenceRepository;
import com.glancy.backend.repository.UserRepository;

/**
 * Stores and retrieves per-user settings such as theme and preferred
 * languages.
 */
@Slf4j
@Service
public class UserPreferenceService {
    private final UserPreferenceRepository userPreferenceRepository;
    private final UserRepository userRepository;

    public UserPreferenceService(UserPreferenceRepository userPreferenceRepository,
                                 UserRepository userRepository) {
        this.userPreferenceRepository = userPreferenceRepository;
        this.userRepository = userRepository;
    }

    /**
     * Save UI and language preferences for a user.
     */
    @Transactional
    public UserPreferenceResponse savePreference(Long userId, UserPreferenceRequest req) {
        log.info("Saving preferences for user {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        UserPreference pref = userPreferenceRepository.findByUserId(userId)
                .orElse(new UserPreference());
        pref.setUser(user);
        pref.setTheme(req.getTheme());
        pref.setSystemLanguage(req.getSystemLanguage());
        pref.setSearchLanguage(req.getSearchLanguage());
        pref.setDictionaryModel(req.getDictionaryModel());
        UserPreference saved = userPreferenceRepository.save(pref);
        return toResponse(saved);
    }

    /**
     * Retrieve preferences previously saved for the user.
     */
    @Transactional(readOnly = true)
    public UserPreferenceResponse getPreference(Long userId) {
        log.info("Fetching preferences for user {}", userId);
        UserPreference pref = userPreferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("未找到用户设置"));
        return toResponse(pref);
    }

    private UserPreferenceResponse toResponse(UserPreference pref) {
        return new UserPreferenceResponse(pref.getId(), pref.getUser().getId(),
                pref.getTheme(), pref.getSystemLanguage(),
                pref.getSearchLanguage(), pref.getDictionaryModel());
    }
}
