
package com.nracademy.backend.exception;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;
import com.nracademy.backend.dto.error.ErrorDetailDTO;

public class EmailAlreadyRegisteredException extends AppException {

    public EmailAlreadyRegisteredException(String message, StatusCode statusCode, List<ErrorDetailDTO> details) {
        super(message, statusCode, details, HttpStatus.CONFLICT);
    }
}