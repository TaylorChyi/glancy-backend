package com.glancy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response representing a stored traffic record.
 */
@Data
@AllArgsConstructor
public class TrafficRecordResponse {
    private Long id;
    private String path;
    private String ip;
    private String userAgent;
    private LocalDateTime createdAt;
}
