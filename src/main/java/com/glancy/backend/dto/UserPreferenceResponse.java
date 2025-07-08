package com.glancy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserPreferenceResponse {
    private Long id;
    private Long userId;
    private String theme;
    private String systemLanguage;
    private String searchLanguage;
}
