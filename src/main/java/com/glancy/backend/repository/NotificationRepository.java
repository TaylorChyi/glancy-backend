package com.glancy.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.glancy.backend.entity.Notification;

/**
 * Accessor for {@link Notification} entities used by notification services.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findBySystemLevelTrue();
    List<Notification> findByUserId(Long userId);
}
