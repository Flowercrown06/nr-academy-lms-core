package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import com.nracademy.backend.exception.AppException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class UserBlockedException extends AppException {
    public UserBlockedException() {
        super("This user account has been blocked.", StatusCode.USER_BLOCKED, List.of(), HttpStatus.FORBIDDEN);
    }
}
