package com.glancy.backend.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.glancy.backend.dto.NotificationRequest;
import com.glancy.backend.dto.NotificationResponse;
import com.glancy.backend.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/system")
    public ResponseEntity<NotificationResponse> createSystem(@Valid @RequestBody NotificationRequest req) {
        NotificationResponse resp = notificationService.createSystemNotification(req);
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<NotificationResponse> createUser(@PathVariable Long userId,
                                                           @Valid @RequestBody NotificationRequest req) {
        NotificationResponse resp = notificationService.createUserNotification(userId, req);
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getForUser(@PathVariable Long userId) {
        List<NotificationResponse> resp = notificationService.getNotificationsForUser(userId);
        return ResponseEntity.ok(resp);
    }
}
