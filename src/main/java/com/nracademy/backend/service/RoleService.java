package com.nracademy.backend.service;

import java.util.List;
import com.nracademy.backend.entity.user.Role;
import com.nracademy.backend.entity.enums.RoleType;

public interface RoleService {
    void saveRole(Role role);
    List<Role> getAllRoles();
    Role getRoleByName(RoleType role);
    void addRoleToUser(String email, RoleType roleType);
    void removeRoleFromUser(String email, RoleType roleType);
}
