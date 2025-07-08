package com.glancy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String message;
    private Boolean systemLevel;
    private Long userId;
}
