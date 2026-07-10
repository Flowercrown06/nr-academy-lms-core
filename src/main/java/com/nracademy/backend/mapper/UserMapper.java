package com.nracademy.backend.mapper;

import com.nracademy.backend.dto.response.AuthUserResponse;
import com.nracademy.backend.dto.response.UserDto;
import com.nracademy.backend.entity.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .courseId(user.getCourseId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public AuthUserResponse toAuthUser(User user) {
        return AuthUserResponse.builder()
                .id(user.getId())
                .courseId(user.getCourseId())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }
}
