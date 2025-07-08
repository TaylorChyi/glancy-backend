package com.glancy.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request body for creating or updating a runtime system parameter.
 */
@Data
public class SystemParameterRequest {
    @NotBlank(message = "{validation.systemParameter.name.notblank}")
    private String name;

    @NotBlank(message = "{validation.systemParameter.value.notblank}")
    private String value;
}
