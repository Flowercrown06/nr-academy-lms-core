package com.nracademy.backend.dto.response;

import com.nracademy.backend.dto.error.ErrorDTO;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorResponse {
    @Builder.Default
    Boolean success = false;
    ErrorDTO error;
}