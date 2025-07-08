package com.glancy.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Payload for binding an external account to a user.
 */
@Data
public class ThirdPartyAccountRequest {
    @NotBlank(message = "平台不能为空")
    private String provider;

    @NotBlank(message = "外部ID不能为空")
    private String externalId;
}
