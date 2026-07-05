
package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;

import com.nracademy.backend.dto.error.ErrorDetailDTO;

public class MissingRequiredFieldException extends AppException {

    public MissingRequiredFieldException(String message, StatusCode statusCode, List<ErrorDetailDTO> details) {
        super(message, statusCode, details, HttpStatus.BAD_REQUEST);
    }
}