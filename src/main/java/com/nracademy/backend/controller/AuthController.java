package com.nracademy.backend.controller;

import com.nracademy.backend.dto.request.ChangePasswordRequest;
import com.nracademy.backend.dto.request.LoginRequest;
import com.nracademy.backend.dto.request.RefreshRequest;
import com.nracademy.backend.dto.request.RegisterRequest;
import com.nracademy.backend.dto.response.AuthResponse;
import com.nracademy.backend.dto.response.UserMeResponse;
import com.nracademy.backend.service.AuthService;
import com.nracademy.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody RefreshRequest request) {
        String accessToken = authHeader.replace("Bearer ", "");
        authService.logout(accessToken, request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> getMe() {
        return ResponseEntity.ok(userService.getUserMe());
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ChangePasswordRequest request) {
        String accessToken = authHeader.replace("Bearer ", "");
        authService.changePassword(accessToken, request);
        return ResponseEntity.noContent().build();
    }
}
