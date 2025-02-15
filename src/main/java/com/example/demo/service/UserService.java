package com.example.demo.service;

import com.example.demo.model.Role;
import com.example.demo.model.Register;

import java.util.List;

public interface UserService {

    void registerNewUser(Register user);
    List<Role> getAllRole();
    Register login(String userName);
    Register loadRegisterByUsername(String username);
}
