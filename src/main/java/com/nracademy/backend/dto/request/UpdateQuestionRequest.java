package com.nracademy.backend.dto.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQuestionRequest {
    private String questionText;
    private BigDecimal points;
    private Integer sortOrder;

    @Valid
    private List<QuestionOptionRequest> options;
}