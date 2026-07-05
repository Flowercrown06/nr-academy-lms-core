package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;

public class UnsupportedFilterException extends AppException {
    public UnsupportedFilterException(String filterName) {
        super("Filter '" + filterName + "' is not supported for this endpoint",
            StatusCode.UNSUPPORTED_FILTER, List.of(), HttpStatus.BAD_REQUEST);
    }
}
