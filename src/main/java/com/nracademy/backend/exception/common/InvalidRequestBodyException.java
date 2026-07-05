package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;

public class InvalidRequestBodyException extends AppException {
    public InvalidRequestBodyException(String message) {
        super(message, StatusCode.INVALID_REQUEST_BODY, List.of(), HttpStatus.BAD_REQUEST);
    }
}
