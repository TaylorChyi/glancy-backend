package com.glancy.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request object for logging portal traffic.
 */
@Data
public class TrafficRecordRequest {
    @NotBlank(message = "{validation.traffic.path.notblank}")
    private String path;
    private String ip;
    private String userAgent;
}
