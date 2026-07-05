package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import com.nracademy.backend.exception.AppException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class UnsupportedSortDirectionException extends AppException {
    public UnsupportedSortDirectionException(String direction) {
        super("Sort direction '" + direction + "' must be 'asc' or 'desc'",
            StatusCode.UNSUPPORTED_SORT_DIRECTION, List.of(), HttpStatus.BAD_REQUEST);
    }
}
