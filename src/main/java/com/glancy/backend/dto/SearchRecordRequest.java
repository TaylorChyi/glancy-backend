package com.glancy.backend.dto;

import com.glancy.backend.entity.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SearchRecordRequest {
    @NotBlank(message = "搜索词不能为空")
    private String term;

    @NotNull(message = "语言不能为空")
    private Language language;
}
