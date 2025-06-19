package com.example.demo.mycourse.service;

import com.example.demo.mycourse.model.Role;
import com.example.demo.mycourse.model.User;

import java.util.List;

public interface UserService {

    User registerNewUser(User user);
    List<Role> getAllRole();
    User login(String userName);
}
