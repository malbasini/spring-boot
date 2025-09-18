package com.example.demo.mycourse.service;

import com.example.demo.mycourse.model.Role;
public interface RoleService {

    void save(Role role);
    void assignSingleRole(Integer userId, String roleName);
    void removeRole(Integer userId, String roleName);
    void transferAdmin(Integer newAdminUserId);
}
