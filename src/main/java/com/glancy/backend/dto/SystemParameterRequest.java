package com.glancy.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request body for creating or updating a runtime system parameter.
 */
@Data
public class SystemParameterRequest {
    @NotBlank(message = "参数名不能为空")
    private String name;

    @NotBlank(message = "参数值不能为空")
    private String value;
}
