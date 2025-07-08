package com.glancy.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request body used when saving user preferences.
 */
@Data
public class UserPreferenceRequest {
    @NotBlank
    private String theme;
    @NotBlank
    private String systemLanguage;
    @NotBlank
    private String searchLanguage;
}
