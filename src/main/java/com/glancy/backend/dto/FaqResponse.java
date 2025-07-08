package com.glancy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FaqResponse {
    private Long id;
    private String question;
    private String answer;
}
