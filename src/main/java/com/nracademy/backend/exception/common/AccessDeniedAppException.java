package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;

/** Named *AppException to avoid clashing with Spring Security's AccessDeniedException. */
public class AccessDeniedAppException extends AppException {
    public AccessDeniedAppException(String message) {
        super(message, StatusCode.ACCESS_DENIED, List.of(), HttpStatus.FORBIDDEN);
    }
}
