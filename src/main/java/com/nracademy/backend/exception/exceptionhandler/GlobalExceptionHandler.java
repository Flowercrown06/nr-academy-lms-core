package com.nracademy.backend.exception.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.nracademy.backend.dto.error.ErrorDTO;
import com.nracademy.backend.dto.response.ErrorResponse;
import com.nracademy.backend.exception.AppException;

import jakarta.validation.ConstraintViolationException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception e) {
        String errorMessage = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "ERROR MESSAGE NOT AVAILABLE";
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(ErrorDTO.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.name())
                    .message(Objects.requireNonNull(errorMessage))
                    .details(List.of())
                    .traceId(UUID.randomUUID().toString())
                    .build()
                )
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException e) {
        String errorMessage = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "ERROR MESSAGE NOT AVAILABLE";
        ErrorResponse response = ErrorResponse.builder()
            .error(ErrorDTO.builder()
                .code(e.getStatusCode().name())
                .message(Objects.requireNonNull(errorMessage))
                .details(e.getDetails())
                .traceId(UUID.randomUUID().toString())
                .build())
            .build();
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "ERROR MESSAGE NOT AVAILABLE";
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(ErrorDTO.builder()
                    .code(HttpStatus.BAD_REQUEST.name())
                    .message(Objects.requireNonNull(errorMessage))
                    .details(List.of())
                    .traceId(UUID.randomUUID().toString())
                    .build()
                )
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
        String errorMessage = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "ERROR MESSAGE NOT AVAILABLE";
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(ErrorDTO.builder()
                    .code(HttpStatus.BAD_REQUEST.name())
                    .message(Objects.requireNonNull(errorMessage))
                    .details(List.of())
                    .traceId(UUID.randomUUID().toString())
                    .build()
                )
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String errorMessage = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "ERROR MESSAGE NOT AVAILABLE";
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(ErrorDTO.builder()
                    .code(HttpStatus.BAD_REQUEST.name())
                    .message(Objects.requireNonNull(errorMessage))
                    .details(List.of())
                    .traceId(UUID.randomUUID().toString())
                    .build()
                )
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}