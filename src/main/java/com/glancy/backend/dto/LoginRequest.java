package com.glancy.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Parameters provided when a user attempts to log in.
 */
@Data
public class LoginRequest {
    private String username;  // 可选
    private String email;     // 可选

    @NotBlank(message = "密码不能为空")
    private String password;

    // Optional device information used during login    private String deviceInfo;}