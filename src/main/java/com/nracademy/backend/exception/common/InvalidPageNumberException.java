package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;

public class InvalidPageNumberException extends AppException {
    public InvalidPageNumberException() {
        super("Page number must be >= 0.", StatusCode.INVALID_PAGINATION_PARAMETERS, List.of(), HttpStatus.BAD_REQUEST);
    }
}
