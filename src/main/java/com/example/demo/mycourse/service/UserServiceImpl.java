package com.example.demo.mycourse.service;

import com.example.demo.mycourse.model.Role;
import com.example.demo.mycourse.model.User;
import com.example.demo.mycourse.repository.RoleRepository;
import com.example.demo.mycourse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;


    @Override
    public User registerNewUser(User user) {
        return userRepository.save(user);
    }
    

    public List<Role> getAllRole()
    {
        return roleRepository.findAll();
    }


    public User login(String userName) {
        User user = userRepository.findByUsername(userName);
        if (user == null)
            throw new IllegalArgumentException("Invalid User Name");
        return user;
    }
}
