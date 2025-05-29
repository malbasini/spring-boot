package com.example.demo.mycourse.service;

import com.example.demo.mycourse.model.Admin;
import com.example.demo.mycourse.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl {

    @Autowired
    private AdminRepository adminRepository;

    public Admin save(Admin admin) {
        return adminRepository.save(admin);
    }
}
