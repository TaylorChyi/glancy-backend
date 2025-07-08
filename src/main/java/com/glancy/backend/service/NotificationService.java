package com.glancy.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glancy.backend.dto.NotificationRequest;
import com.glancy.backend.dto.NotificationResponse;
import com.glancy.backend.entity.Notification;
import com.glancy.backend.entity.User;
import com.glancy.backend.repository.NotificationRepository;
import com.glancy.backend.repository.UserRepository;

/**
 * Business logic around creating and listing notifications that may
 * either be global announcements or user specific.
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a system level notification for all users.
     */
    @Transactional
    public NotificationResponse createSystemNotification(NotificationRequest request) {
        Notification notification = new Notification();
        notification.setMessage(request.getMessage());
        notification.setSystemLevel(true);
        Notification saved = notificationRepository.save(notification);
        return toResponse(saved);
    }

    /**
     * Create a notification for a single user.
     */
    @Transactional
    public NotificationResponse createUserNotification(Long userId, NotificationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        Notification notification = new Notification();
        notification.setMessage(request.getMessage());
        notification.setSystemLevel(false);
        notification.setUser(user);
        Notification saved = notificationRepository.save(notification);
        return toResponse(saved);
    }

    /**
     * Combine system notifications with those addressed to the given user.
     */
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsForUser(Long userId) {
        List<Notification> result = new ArrayList<>();
        result.addAll(notificationRepository.findBySystemLevelTrue());
        result.addAll(notificationRepository.findByUserId(userId));
        return result.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private NotificationResponse toResponse(Notification n) {
        Long uid = n.getUser() != null ? n.getUser().getId() : null;
        return new NotificationResponse(n.getId(), n.getMessage(), n.getSystemLevel(), uid);
    }
}
