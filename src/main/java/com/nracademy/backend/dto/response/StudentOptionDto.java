package com.nracademy.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Student-safe option view.
 * CRITICAL: this class must never gain a `correct` field.
 * If you need one, you are building the wrong DTO — use TeacherOptionDto instead.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentOptionDto {
    UUID id;
    String optionText;
}