package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;

public class InvalidFilterValueException extends AppException {
    public InvalidFilterValueException(String filterName, String value) {
        super("Invalid value '" + value + "' for filter '" + filterName + "'",
            StatusCode.INVALID_FILTER_VALUE, List.of(), HttpStatus.BAD_REQUEST);
    }
}
