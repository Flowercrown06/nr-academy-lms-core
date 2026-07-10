package com.nracademy.backend.dto.request;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String surname;
    private String phone;
}
