package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;

public class PageSizeExceededException extends AppException {
    public PageSizeExceededException(int requestedSize, int maxSize) {
        super("Requested page size " + requestedSize + " exceeds maximum of " + maxSize,
            StatusCode.PAGE_SIZE_EXCEEDED, List.of(), HttpStatus.BAD_REQUEST);
    }
}
