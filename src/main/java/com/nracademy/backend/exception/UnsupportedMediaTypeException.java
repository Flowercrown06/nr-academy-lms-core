
package com.nracademy.backend.exception;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;

import com.nracademy.backend.dto.error.ErrorDetailDTO;

public class UnsupportedMediaTypeException extends AppException {

    public UnsupportedMediaTypeException(String message, StatusCode statusCode, List<ErrorDetailDTO> details) {
        super(message, statusCode, details, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
}