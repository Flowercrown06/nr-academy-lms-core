package com.nracademy.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Student-safe question view.
 * CRITICAL: options must be StudentOptionDto, never TeacherOptionDto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentQuestionDto {
    UUID id;
    String questionText;
    BigDecimal points;
    Integer sortOrder;
    List<StudentOptionDto> options;
}