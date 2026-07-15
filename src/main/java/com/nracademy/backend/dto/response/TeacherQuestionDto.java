package com.nracademy.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** Full question view for teachers/course owners, options include `correct`. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherQuestionDto {
    UUID id;
    String questionText;
    BigDecimal points;
    Integer sortOrder;
    List<TeacherOptionDto> options;
}