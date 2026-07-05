package com.nracademy.backend;

import com.nracademy.backend.entity.enums.RoleType;
import com.nracademy.backend.entity.user.Role;
import com.nracademy.backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        for (RoleType type : RoleType.values()) {
            if (roleRepository.getRoleByName(type).isEmpty()) {
                Role role = Role.builder().name(type).build();
                roleRepository.save(role);
            }
        }
    }
}