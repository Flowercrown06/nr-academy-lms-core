package com.nracademy.backend.exception.common;

import com.nracademy.backend.dto.error.ErrorDetailDTO;
import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;

public class PasswordMismatchException extends AppException {
    public PasswordMismatchException(String message, StatusCode code, List<ErrorDetailDTO> details) {
        super(message, code, details, HttpStatus.BAD_REQUEST);
    }
}