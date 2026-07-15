package com.nracademy.backend.service.impl;

import com.nracademy.backend.common.api.PageResponse;
import com.nracademy.backend.dto.error.ErrorDetailDTO;
import com.nracademy.backend.dto.request.CreateUserRequest;
import com.nracademy.backend.dto.request.RegisterRequest;
import com.nracademy.backend.dto.request.UpdateUserRequest;
import com.nracademy.backend.dto.request.UserStatusUpdateRequest;
import com.nracademy.backend.dto.response.UserDto;
import com.nracademy.backend.dto.response.UserMeResponse;
import com.nracademy.backend.entity.enums.Role;
import com.nracademy.backend.entity.enums.StatusCode;
import com.nracademy.backend.entity.enums.UserStatus;
import com.nracademy.backend.entity.User;
import com.nracademy.backend.exception.common.EmailAlreadyRegisteredException;
import com.nracademy.backend.exception.common.UnauthenticatedException;
import com.nracademy.backend.exception.common.UserEmailNotFoundException;
import com.nracademy.backend.repository.UserRepository;
import com.nracademy.backend.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional
                .orElseThrow(() -> new UserEmailNotFoundException(
                        "User not found by email.",
                        StatusCode.USER_NOT_FOUND,
                        List.of(new ErrorDetailDTO("email", email))
                ));
    }

    @Override
    @Transactional
    public User createUser(RegisterRequest request) {
        validateEmailExists(request.getEmail(), null);
        User user = User.builder()
                .email(request.getEmail())
                .name(request.getEmail())
                .surname("")
                .role(Role.STUDENT)
                .status(UserStatus.ACTIVE)
                .build();
        return userRepository.save(user);
    }

    @Override
    public UserMeResponse getUserMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        checkAuthentication(authentication);
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserEmailNotFoundException(
                "User email not found: " + email,
                StatusCode.USER_EMAIL_NOT_FOUND, List.of()
        ));
        return mapToUserMeResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UUID> getUserIds() {
        return userRepository.findIdsByStatus(UserStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserReference(UUID userId) {
        return userRepository.getReferenceById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userExistsById(UUID id) {
        return userRepository.existsById(id);
    }

    @Override
    public PageResponse<UserDto> listUsersForSuperAdmin(UUID courseId, Role role, UserStatus status, String q, Instant createdFrom, Instant createdTo, int page, int size, List<String> sort) {
        return null;
    }

    @Override
    public UserDto getUserForSuperAdmin(UUID userId) {
        return null;
    }

    @Override
    public UserDto createUserForSuperAdmin(CreateUserRequest request) {
        return null;
    }

    @Override
    public UserDto updateUserForSuperAdmin(UUID userId, UpdateUserRequest request) {
        return null;
    }

    @Override
    public UserDto updateUserStatusForSuperAdmin(UUID userId, UserStatusUpdateRequest request) {
        return null;
    }

    private void validateEmailExists(String email, UUID userId) {
        boolean existsByEmail = userId == null ?
                userRepository.existsByEmail(email) :
                userRepository.existsByEmailAndIdNot(email, userId);
        if (existsByEmail) {
            throw new EmailAlreadyRegisteredException(
                    "Email already registered. Email: " + email,
                    StatusCode.EMAIL_ALREADY_REGISTERED, List.of());
        }
    }

    private void checkAuthentication(Authentication authentication) {
        if (!authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthenticatedException(
                    "Unauthorized", StatusCode.UNAUTHENTICATED, List.of());
        }
    }

    private UserMeResponse mapToUserMeResponse(User user) {
        return UserMeResponse.builder()
                .id(user.getId())
                .courseId(user.getCourseId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}
