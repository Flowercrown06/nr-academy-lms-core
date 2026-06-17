package com.nracademy.backend.service;

import java.util.List;
import java.util.UUID;
import com.nracademy.backend.entity.user.User;
import com.nracademy.backend.dto.response.UserMeResponse;
import com.nracademy.backend.dto.request.RegisterRequest;

public interface UserService {
    User getUserByEmail(String email);
    User createUser(RegisterRequest request);
    UserMeResponse getUserMe();
    List<UUID> getUserIds();
    User getUserReference(UUID userId);
    boolean userExistsById(UUID id);
}
