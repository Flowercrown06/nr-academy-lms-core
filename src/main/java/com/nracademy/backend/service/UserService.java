package com.nracademy.backend.service;

import com.nracademy.backend.common.api.PageResponse;
import com.nracademy.backend.dto.request.CreateUserRequest;
import com.nracademy.backend.dto.request.RegisterRequest;
import com.nracademy.backend.dto.request.UpdateUserRequest;
import com.nracademy.backend.dto.request.UserStatusUpdateRequest;
import com.nracademy.backend.dto.response.UserDto;
import com.nracademy.backend.dto.response.UserMeResponse;
import com.nracademy.backend.entity.User;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserService {
    User getUserByEmail(String email);
    User createUser(RegisterRequest request);
    UserMeResponse getUserMe();
    List<UUID> getUserIds();
    User getUserReference(UUID userId);
    boolean userExistsById(UUID id);

    PageResponse<UserDto> listUsersForSuperAdmin(
            UUID courseId,
            com.nracademy.backend.entity.enums.Role role,
            com.nracademy.backend.entity.enums.UserStatus status,
            String q,
            Instant createdFrom,
            Instant createdTo,
            int page,
            int size,
            List<String> sort);

    UserDto getUserForSuperAdmin(UUID userId);
    UserDto createUserForSuperAdmin(CreateUserRequest request);
    UserDto updateUserForSuperAdmin(UUID userId, UpdateUserRequest request);
    UserDto updateUserStatusForSuperAdmin(UUID userId, UserStatusUpdateRequest request);
}
