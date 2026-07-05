package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;

public class UnsupportedSortFieldException extends AppException {
    public UnsupportedSortFieldException(String field) {
        super("Sort field '" + field + "' is not supported for this endpoint",
            StatusCode.UNSUPPORTED_SORT_FIELD, List.of(), HttpStatus.BAD_REQUEST);
    }
}
