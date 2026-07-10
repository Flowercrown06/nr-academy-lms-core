package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;

public class InvalidRoleException extends AppException {
    public InvalidRoleException() {
        super("Role value is not supported for this operation.", StatusCode.INVALID_ROLE, List.of(), HttpStatus.BAD_REQUEST);
    }
}
