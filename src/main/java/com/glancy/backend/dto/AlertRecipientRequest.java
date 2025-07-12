package com.glancy.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Payload for creating a new alert recipient email address.
 */
@Data
public class AlertRecipientRequest {
    @NotBlank(message = "{validation.alertRecipient.email.notblank}")
    @Email(message = "邮箱格式不正确")
    private String email;
}
