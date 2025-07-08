package com.glancy.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificationRequest {
    @NotBlank(message = "通知内容不能为空")
    private String message;
}
