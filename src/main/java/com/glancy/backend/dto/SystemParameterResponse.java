package com.glancy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response object representing a single system parameter.
 */
@Data
@AllArgsConstructor
public class SystemParameterResponse {
    private Long id;
    private String name;
    private String value;
}
