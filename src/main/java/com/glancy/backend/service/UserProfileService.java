package com.glancy.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import com.glancy.backend.dto.UserProfileRequest;
import com.glancy.backend.dto.UserProfileResponse;
import com.glancy.backend.entity.User;
import com.glancy.backend.entity.UserProfile;
import com.glancy.backend.repository.UserProfileRepository;
import com.glancy.backend.repository.UserRepository;

/**
 * Manage optional personal details for users.
 */
@Slf4j
@Service
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    public UserProfileService(UserProfileRepository userProfileRepository,
                              UserRepository userRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
    }

    /**
     * Save the profile for a user.
     */
    @Transactional
    public UserProfileResponse saveProfile(Long userId, UserProfileRequest req) {
        log.info("Saving profile for user {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElse(new UserProfile());
        profile.setUser(user);
        profile.setAge(req.getAge());
        profile.setGender(req.getGender());
        profile.setJob(req.getJob());
        profile.setInterest(req.getInterest());
        profile.setGoal(req.getGoal());
        UserProfile saved = userProfileRepository.save(profile);
        return toResponse(saved);
    }

    /**
     * Fetch profile for a user.
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        log.info("Fetching profile for user {}", userId);
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("未找到用户配置"));
        return toResponse(profile);
    }

    private UserProfileResponse toResponse(UserProfile profile) {
        return new UserProfileResponse(profile.getId(), profile.getUser().getId(),
                profile.getAge(), profile.getGender(), profile.getJob(),
                profile.getInterest(), profile.getGoal());
    }
}
