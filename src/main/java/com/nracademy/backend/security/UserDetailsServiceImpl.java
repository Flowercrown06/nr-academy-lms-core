package com.nracademy.backend.security;

import com.nracademy.backend.entity.user.Role;
import com.nracademy.backend.entity.user.User;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.nracademy.backend.dto.error.ErrorDetailDTO;
import com.nracademy.backend.entity.enums.StatusCode;
import com.nracademy.backend.exception.common.UserEmailNotFoundException;
import com.nracademy.backend.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserEmailNotFoundException(
                        "User email not found: " + email,
                        StatusCode.USER_NOT_FOUND,
                        List.of(new ErrorDetailDTO("email", email))
                ));

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        Set<Role> roles = user.getRoles();

        for (Role role : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().name()));
        }

        return UserDetailsImpl.builder()
                .baseAuthorities(grantedAuthorities)
                .email(user.getEmail())
                .password(user.getPassword())
                .enabled(user.isActive())
                .build();
    }

    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken)
            return false;
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }
}