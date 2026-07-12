package com.nracademy.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateQuestionRequest {
    @NotBlank
    private String questionText;

    @NotNull
    private BigDecimal points;

    @NotNull
    private Integer sortOrder;

    @NotEmpty
    @Valid
    private List<QuestionOptionRequest> options;
}