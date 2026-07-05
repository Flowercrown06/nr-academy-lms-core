package com.nracademy.backend.service.impl;

import com.nracademy.backend.dto.error.ErrorDetailDTO;
import com.nracademy.backend.entity.user.Role;
import com.nracademy.backend.entity.user.User;
import com.nracademy.backend.entity.enums.RoleType;
import com.nracademy.backend.entity.enums.StatusCode;
import com.nracademy.backend.exception.common.RoleNotFoundException;
import com.nracademy.backend.exception.common.UserEmailNotFoundException;
import com.nracademy.backend.repository.RoleRepository;
import com.nracademy.backend.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.nracademy.backend.service.RoleService;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveRole(Role role) {
        roleRepository.save(role);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleByName(RoleType role) {
        return roleRepository.getRoleByName(role)
            .orElseThrow(() -> new RoleNotFoundException(
                "Role not found.", 
                StatusCode.ROLE_NOT_FOUND, 
                List.of(new ErrorDetailDTO("role", role.name())))
            );
    }

    @Transactional
    public void addRoleToUser(String email, RoleType roleType) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = userOptional
            .orElseThrow(() -> new UserEmailNotFoundException(
                "User not found by email.", 
                StatusCode.USER_EMAIL_NOT_FOUND, 
                List.of(new ErrorDetailDTO("email", email)))
            );
        
        Role role = getRoleByName(roleType);

        user.addRole(role);
    }

    @Transactional
    public void removeRoleFromUser(String email, RoleType roleType) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = userOptional
            .orElseThrow(() -> new UserEmailNotFoundException(
                "User not found by email.", 
                StatusCode.USER_EMAIL_NOT_FOUND, 
                List.of(new ErrorDetailDTO("email", email)))
            );
        
        Role role = getRoleByName(roleType);
        user.removeRole(role);
    }
}