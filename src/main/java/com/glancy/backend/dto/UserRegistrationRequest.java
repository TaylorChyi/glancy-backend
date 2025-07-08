package com.glancy.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Payload for creating a new user account.
 */
@Data
public class UserRegistrationRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度需在3到50之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度至少为6位")
    private String password;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    // Optional avatar URL
    private String avatar;

    // Optional phone number    private String phone;}