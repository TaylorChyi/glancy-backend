package com.glancy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Daily active user statistics response.
 */
@Data
@AllArgsConstructor
public class DailyActiveUserResponse {
    private long activeUsers;
    private double activeRate;
}
