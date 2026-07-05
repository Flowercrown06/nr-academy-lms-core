package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import com.nracademy.backend.exception.AppException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class InvalidEnumValueException extends AppException {
    public InvalidEnumValueException(String fieldName, String value) {
        super("Invalid value '" + value + "' for field '" + fieldName + "'",
            StatusCode.INVALID_ENUM_VALUE, List.of(), HttpStatus.BAD_REQUEST);
    }
}
