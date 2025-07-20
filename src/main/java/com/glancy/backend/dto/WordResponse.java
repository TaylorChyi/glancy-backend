package com.glancy.backend.dto;

import com.glancy.backend.entity.Language;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WordResponse {
    private String id;
    private String term;
    private List<String> definitions;
    private Language language;
    private String example;
    private String phonetic;
}
