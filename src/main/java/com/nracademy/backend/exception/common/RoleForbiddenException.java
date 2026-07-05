package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;

public class RoleForbiddenException extends AppException {
    public RoleForbiddenException(String message) {
        super(message, StatusCode.ROLE_FORBIDDEN, List.of(), HttpStatus.FORBIDDEN);
    }
}
