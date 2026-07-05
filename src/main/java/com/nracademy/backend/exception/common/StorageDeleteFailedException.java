
package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;

import com.nracademy.backend.dto.error.ErrorDetailDTO;

public class StorageDeleteFailedException extends AppException {

    public StorageDeleteFailedException(String message, StatusCode statusCode, List<ErrorDetailDTO> details) {
        super(message, statusCode, details, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}