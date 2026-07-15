package com.nracademy.backend.common.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    @Builder.Default
    private boolean success = true;
    private T data;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
