package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;

public class InvalidPaymentPerStudentException extends AppException {
    public InvalidPaymentPerStudentException() {
        super("Payment per student must be >= 0.", StatusCode.INVALID_PAYMENT_PER_STUDENT, List.of(), HttpStatus.BAD_REQUEST);
    }
}
