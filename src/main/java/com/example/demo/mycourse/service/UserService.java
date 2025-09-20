package com.example.demo.mycourse.service;

import com.example.demo.mycourse.model.User;

public interface UserService {

    User registerNewUser(User user);
    User login(String userName);
}
