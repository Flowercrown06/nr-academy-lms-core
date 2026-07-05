package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import com.nracademy.backend.exception.AppException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class UnsupportedSearchFieldException extends AppException {
    public UnsupportedSearchFieldException(String field) {
        super("Search field '" + field + "' is not supported for this endpoint",
            StatusCode.UNSUPPORTED_SEARCH_FIELD, List.of(), HttpStatus.BAD_REQUEST);
    }
}
