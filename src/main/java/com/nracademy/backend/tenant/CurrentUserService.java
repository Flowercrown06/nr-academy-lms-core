package com.nracademy.backend.tenant;

import com.nracademy.backend.entity.User;
import com.nracademy.backend.exception.common.UnauthenticatedException;
import com.nracademy.backend.repository.UserRepository;
import com.nracademy.backend.entity.enums.StatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User requireCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthenticatedException("Unauthorized", StatusCode.UNAUTHENTICATED, List.of());
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UnauthenticatedException("Unauthorized", StatusCode.UNAUTHENTICATED, List.of()));
    }
}
