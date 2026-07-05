package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;

public class FilterCombinationNotAllowedException extends AppException {
    public FilterCombinationNotAllowedException(String message) {
        super(message, StatusCode.FILTER_COMBINATION_NOT_ALLOWED, List.of(), HttpStatus.BAD_REQUEST);
    }
}
