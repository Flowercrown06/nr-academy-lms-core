package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import com.nracademy.backend.exception.AppException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class TenantAccessDeniedException extends AppException {
    public TenantAccessDeniedException() {
        super("You do not have access to this resource.",
            StatusCode.TENANT_ACCESS_DENIED, List.of(), HttpStatus.FORBIDDEN);
    }
}
