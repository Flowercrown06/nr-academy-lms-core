package com.nracademy.backend.repository;

import com.nracademy.backend.entity.user.Role;
import com.nracademy.backend.entity.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> getRoleByName(RoleType name);
}