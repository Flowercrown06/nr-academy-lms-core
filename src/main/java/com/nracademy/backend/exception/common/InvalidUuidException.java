package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;

public class InvalidUuidException extends AppException {
    public InvalidUuidException(String fieldName, String value) {
        super("Invalid UUID value '" + value + "' for field '" + fieldName + "'",
            StatusCode.INVALID_UUID, List.of(), HttpStatus.BAD_REQUEST);
    }
}
