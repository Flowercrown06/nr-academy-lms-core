package com.nracademy.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Full option view for teachers/course owners. Includes `correct` on purpose. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherOptionDto {
    UUID id;
    String optionText;
    Boolean correct;
    Integer sortOrder;
}