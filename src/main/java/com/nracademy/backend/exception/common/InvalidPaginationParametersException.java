package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;

public class InvalidPaginationParametersException extends AppException {
    public InvalidPaginationParametersException(String message) {
        super(message, StatusCode.INVALID_PAGINATION_PARAMETERS, List.of(), HttpStatus.BAD_REQUEST);
    }
}
