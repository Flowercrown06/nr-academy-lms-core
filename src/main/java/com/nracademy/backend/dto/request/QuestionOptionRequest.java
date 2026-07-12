package com.nracademy.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuestionOptionRequest {
    @NotBlank
    private String optionText;

    @NotNull
    private Boolean correct;

    @NotNull
    private Integer sortOrder;
}