package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;

public class InvalidCommissionRateException extends AppException {
    public InvalidCommissionRateException() {
        super("Commission rate must be between 0 and 100.", StatusCode.INVALID_COMMISSION_RATE, List.of(), HttpStatus.BAD_REQUEST);
    }
}
