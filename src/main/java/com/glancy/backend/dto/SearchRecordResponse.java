package com.glancy.backend.dto;

import com.glancy.backend.entity.Language;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SearchRecordResponse {
    private Long id;
    private Long userId;
    private String term;
    private Language language;
    private LocalDateTime createdAt;
}
