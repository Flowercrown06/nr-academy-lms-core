package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

public class UserNotFoundException extends AppException {
    public UserNotFoundException(UUID userId) {
        super("User was not found: " + userId, StatusCode.USER_NOT_FOUND, List.of(), HttpStatus.NOT_FOUND);
    }
}
