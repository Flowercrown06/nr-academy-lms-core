package com.nracademy.backend.service.impl;

import com.nracademy.backend.dto.request.ChangePasswordRequest;
import com.nracademy.backend.dto.request.LoginRequest;
import com.nracademy.backend.dto.request.RefreshRequest;
import com.nracademy.backend.dto.request.RegisterRequest;
import com.nracademy.backend.dto.response.AuthResponse;
import com.nracademy.backend.entity.auth.RefreshToken;
import com.nracademy.backend.entity.enums.BlacklistReason;
import com.nracademy.backend.entity.enums.RoleType;
import com.nracademy.backend.entity.enums.StatusCode;
import com.nracademy.backend.entity.user.User;
import com.nracademy.backend.exception.EmailAlreadyRegisteredException;
import com.nracademy.backend.exception.InvalidCurrentPasswordException;
import com.nracademy.backend.exception.InvalidRefreshTokenException;
import com.nracademy.backend.exception.PasswordMismatchException;
import com.nracademy.backend.exception.UnauthenticatedException;
import com.nracademy.backend.exception.UserEmailNotFoundException;
import com.nracademy.backend.repository.RefreshTokenRepository;
import com.nracademy.backend.repository.UserRepository;
import com.nracademy.backend.service.AuthService;
import com.nracademy.backend.service.BlackListService;
import com.nracademy.backend.service.RoleService;
import com.nracademy.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RoleService roleService;
    private final BlackListService blackListService;

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException(
                    "Passwords do not match", StatusCode.PASSWORD_MISMATCH, List.of());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyRegisteredException(
                    "Email already registered", StatusCode.EMAIL_ALREADY_REGISTERED, List.of());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);
        roleService.addRoleToUser(user.getEmail(), RoleType.USER);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // this line triggers AuthenticationProviderImpl.authenticate() under the hood
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String accessToken = jwtUtil.generateAccessToken(request.getEmail());
        String refreshTokenStr = jwtUtil.generateRefreshToken(request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserEmailNotFoundException(
                        "User not found", StatusCode.USER_EMAIL_NOT_FOUND, List.of()));

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenStr)
                .user(user)
                .expiryDate(jwtUtil.extractExpirationDateFromToken(refreshTokenStr)
                        .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build();
        refreshTokenRepository.save(refreshToken);
        user.updateLastLogin();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .build();
    }
    @Override
    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken stored = refreshTokenRepository.findByTokenAndRevokedFalse(request.getRefreshToken())
                .orElseThrow(() -> new InvalidRefreshTokenException(
                        "Invalid refresh token", StatusCode.INVALID_REFRESH_TOKEN, List.of()));

        if (stored.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidRefreshTokenException(
                    "Refresh token expired", StatusCode.REFRESH_TOKEN_EXPIRED, List.of());
        }

        String email = stored.getUser().getEmail();

        // 1. revoke the old one — this UPDATE happens because `stored` is a managed entity
        //    and @Transactional auto-flushes the change at commit
        stored.setRevoked(true);
        stored.setRevokedAt(LocalDateTime.now());

        // 2. issue new tokens
        String newAccessToken = jwtUtil.generateAccessToken(email);
        String newRefreshTokenStr = jwtUtil.generateRefreshToken(email);

        // 3. persist the new refresh token as its own row (this is the missing INSERT)
        RefreshToken newRefreshToken = RefreshToken.builder()
                .token(newRefreshTokenStr)
                .user(stored.getUser())
                .expiryDate(jwtUtil.extractExpirationDateFromToken(newRefreshTokenStr)
                        .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .revoked(false)
                .build();
        refreshTokenRepository.save(newRefreshToken);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshTokenStr)
                .build();
    }

    @Override
    @Transactional
    public void logout(String accessToken, String refreshToken) {
        blackListService.blacklistToken(accessToken, BlacklistReason.USER_LOGOUT);

        RefreshToken rt = refreshTokenRepository.findByTokenAndRevokedFalse(refreshToken)
                .orElseThrow(() -> new InvalidRefreshTokenException(
                        "Invalid refresh token", StatusCode.INVALID_REFRESH_TOKEN, List.of()));

        rt.setRevoked(true);
        rt.setRevokedAt(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void changePassword(String accessToken, ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthenticatedException("Unauthorized", StatusCode.UNAUTHENTICATED, List.of());
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserEmailNotFoundException(
                        "User not found", StatusCode.USER_EMAIL_NOT_FOUND, List.of()));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCurrentPasswordException(
                    "Current password is incorrect", StatusCode.INVALID_CURRENT_PASSWORD, List.of());
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Force re-login everywhere: blacklist the token used for this request,
        // and revoke every other active refresh token this user holds.
        blackListService.blacklistToken(accessToken, BlacklistReason.PASSWORD_CHANGED);

        List<RefreshToken> activeRefreshTokens =
                refreshTokenRepository.findAllByUser_IdAndRevokedFalse(user.getId());
        LocalDateTime now = LocalDateTime.now();
        activeRefreshTokens.forEach(rt -> {
            rt.setRevoked(true);
            rt.setRevokedAt(now);
        });
    }
}
