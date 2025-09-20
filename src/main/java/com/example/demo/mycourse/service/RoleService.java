package com.example.demo.mycourse.service;

public interface RoleService {

    void assignSingleRole(Integer userId, String roleName);
    void removeRole(Integer userId, String roleName);
}
