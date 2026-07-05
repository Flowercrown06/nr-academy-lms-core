package com.nracademy.backend.service;

import com.nracademy.backend.dto.request.ChangePasswordRequest;
import com.nracademy.backend.dto.request.LoginRequest;
import com.nracademy.backend.dto.request.RefreshRequest;
import com.nracademy.backend.dto.request.RegisterRequest;
import com.nracademy.backend.dto.response.AuthResponse;

public interface AuthService {
    void register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refresh(RefreshRequest request);
    void logout(String accessToken, String refreshToken);
    void changePassword(String accessToken, ChangePasswordRequest request);
}
