package com.example.demo.mycourse.service;

import com.example.demo.mycourse.model.Role;
import com.example.demo.mycourse.model.User;

import java.util.List;

public interface UserService {

    void registerNewUser(String username,String fullname, String password, String email, String role);
    List<Role> getAllRole();
    User login(String userName);
}
