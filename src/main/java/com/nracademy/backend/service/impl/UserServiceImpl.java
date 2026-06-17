package com.nracademy.backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.nracademy.backend.dto.request.RegisterRequest;
import com.nracademy.backend.dto.request.UpdateUserMeRequest;
import com.nracademy.backend.dto.response.*;
import com.nracademy.backend.entity.user.User;
import com.nracademy.backend.entity.user.Role;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nracademy.backend.dto.error.ErrorDetailDTO;
import com.nracademy.backend.entity.enums.RoleType;
import com.nracademy.backend.entity.enums.StatusCode;
import com.nracademy.backend.repository.UserRepository;
import com.nracademy.backend.service.UserService;
import com.nracademy.backend.service.S3Service;
import com.nracademy.backend.service.RoleService;

import lombok.RequiredArgsConstructor;

import com.nracademy.backend.exception.*;

import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    RoleServiceImpl roleService;
    PasswordEncoder passwordEncoder;
    S3Service s3Service;

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
        User user = new User();
        String email = request.getEmail();
        validateEmailExists(email, null);
        user.setEmail(email);
        User newUser = userRepository.save(user);
        roleService.addRoleToUser(user.getEmail(), RoleType.USER);
        return newUser;
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
        return userRepository.findIdsByIsActiveTrue();
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

    private void validateEmailExists(String email, UUID userId) {
        boolean existsByEmail = userId == null ?
                userRepository.existsByEmail(email) :
                userRepository.existsByEmailAndIdNot(email, userId);
        if (existsByEmail)
            throw new EmailAlreadyRegisteredException(
                    "Email already registered. Email: " + email,
                    StatusCode.EMAIL_ALREADY_REGISTERED, List.of());
    }

    private void checkAuthentication(Authentication authentication) {
        if (!authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthenticatedException(
                    "Unauthorized", StatusCode.UNAUTHENTICATED, List.of());
    }

    private UserMeResponse mapToUserMeResponse(User user) {
        Set<RoleType> roleTypes = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        return UserMeResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .isActive(user.isActive())
                .roles(roleTypes)
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}