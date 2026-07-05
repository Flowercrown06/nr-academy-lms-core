package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;

public class InvalidDateRangeException extends AppException {
    public InvalidDateRangeException(String message) {
        super(message, StatusCode.INVALID_DATE_RANGE, List.of(), HttpStatus.BAD_REQUEST);
    }
}
