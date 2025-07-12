package com.glancy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO representing an alert recipient email address.
 */
@Data
@AllArgsConstructor
public class AlertRecipientResponse {
    private Long id;
    private String email;
}
