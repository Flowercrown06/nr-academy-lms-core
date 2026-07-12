package com.nracademy.backend.dto.request;

import jakarta.validation.Valid;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateQuestionRequest {
    private String questionText;
    private BigDecimal points;
    private Integer sortOrder;

    @Valid
    private List<QuestionOptionRequest> options;
}