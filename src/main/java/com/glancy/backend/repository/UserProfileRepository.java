package com.glancy.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.glancy.backend.entity.UserProfile;

import java.util.Optional;

/**
 * Repository for {@link UserProfile} entities.
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUserId(Long userId);
}
