package com.glancy.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Parameters provided when a user attempts to log in.
 */
@Data
public class LoginRequest {
    /**
     * Identifier containing the raw text and resolved type.
     */
    private LoginIdentifier identifier;

    @NotBlank(message = "{validation.login.password.notblank}")
    private String password;
    // Optional device information used during login
    private String deviceInfo;
}
