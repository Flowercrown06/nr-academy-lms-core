package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import com.nracademy.backend.exception.AppException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class InvalidSearchQueryException extends AppException {
    public InvalidSearchQueryException(String message) {
        super(message, StatusCode.INVALID_SEARCH_QUERY, List.of(), HttpStatus.BAD_REQUEST);
    }
}
