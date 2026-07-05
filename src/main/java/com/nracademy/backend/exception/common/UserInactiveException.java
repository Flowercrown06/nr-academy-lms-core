package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import com.nracademy.backend.exception.AppException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class UserInactiveException extends AppException {
    public UserInactiveException() {
        super("This user account is inactive.", StatusCode.USER_INACTIVE, List.of(), HttpStatus.FORBIDDEN);
    }
}
