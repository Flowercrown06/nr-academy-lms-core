package com.nracademy.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TeacherQuestionDto {
    UUID id;
    String questionText;
    BigDecimal points;
    Integer sortOrder;
    List<TeacherOptionDto> options;
}