package com.nracademy.backend.exception.exceptionhandler;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.nracademy.backend.dto.error.ErrorDTO;
import com.nracademy.backend.dto.response.ErrorResponse;
import com.nracademy.backend.entity.enums.StatusCode;
import com.nracademy.backend.exception.AppException;

import jakarta.validation.ConstraintViolationException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {

    /** Builds the standard error envelope so every handler below stays one-liner-short. */
    private ResponseEntity<ErrorResponse> buildResponse(String code, String message, HttpStatus status) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(ErrorDTO.builder()
                    .code(code)
                    .message(Objects.requireNonNullElse(message, "ERROR MESSAGE NOT AVAILABLE"))
                    .details(List.of())
                    .traceId(UUID.randomUUID().toString())
                    .build()
                )
                .build();
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception e) {
        // Never leak internal stack traces / raw messages for unmapped 5xx errors.
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR.name(),
            "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParam(MissingServletRequestParameterException e) {
        return buildResponse(StatusCode.MISSING_REQUIRED_FIELD.name(),
            "Missing required parameter: " + e.getParameterName(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String field = e.getName();
        String value = String.valueOf(e.getValue());
        boolean looksLikeUuidField = field != null && field.toLowerCase().endsWith("id");
        return looksLikeUuidField
            ? buildResponse(StatusCode.INVALID_UUID.name(), "Invalid UUID value '" + value + "' for '" + field + "'", HttpStatus.BAD_REQUEST)
            : buildResponse(StatusCode.INVALID_FILTER_VALUE.name(), "Invalid value '" + value + "' for '" + field + "'", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        return buildResponse(StatusCode.DATA_INTEGRITY_VIOLATION.name(),
            "The request conflicts with existing data (duplicate or constraint violation).", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = OptimisticLockingFailureException.class)
    public ResponseEntity<?> handleOptimisticLocking(OptimisticLockingFailureException e) {
        return buildResponse("OPTIMISTIC_LOCKING_FAILURE",
            "This record was modified by another request. Please reload and try again.", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return buildResponse(StatusCode.VALIDATION_FAILED.name(), e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
        return buildResponse(StatusCode.VALIDATION_FAILED.name(), e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return buildResponse(StatusCode.INVALID_REQUEST_BODY.name(),
            "Request body is missing or malformed.", HttpStatus.BAD_REQUEST);
    }

    // Kept last and most specific: your own AppException subclasses already carry their
    // own status code + http status, so this handler just forwards them as-is.
    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException e) {
        ErrorResponse response = ErrorResponse.builder()
            .error(ErrorDTO.builder()
                .code(e.getStatusCode().name())
                .message(Objects.requireNonNullElse(e.getLocalizedMessage(), "ERROR MESSAGE NOT AVAILABLE"))
                .details(e.getDetails())
                .traceId(UUID.randomUUID().toString())
                .build())
            .build();
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

}