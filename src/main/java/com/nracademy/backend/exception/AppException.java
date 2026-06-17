package com.nracademy.backend.exception;

import com.nracademy.backend.dto.error.ErrorDetailDTO;
import com.nracademy.backend.entity.enums.StatusCode;

import lombok.Getter;

import java.util.List;

import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException {
    private final StatusCode statusCode;
    private final List<ErrorDetailDTO> details;
    private final HttpStatus httpStatus;

    public AppException(
        String message, 
        StatusCode statusCode, 
        List<ErrorDetailDTO> details, 
        HttpStatus httpStatus
    ) {
        super(message);
        this.statusCode = statusCode;
        this.details = details;
        this.httpStatus = httpStatus;
    }
}