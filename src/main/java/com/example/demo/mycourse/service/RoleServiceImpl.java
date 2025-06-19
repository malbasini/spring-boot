package com.example.demo.mycourse.service;

import com.example.demo.mycourse.model.Role;
import com.example.demo.mycourse.repository.RoleRepository;
import com.example.demo.mycourse.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService{
    @Autowired
    private RoleRepository roleRepository;
    @Override
    public void save(Role role) {
        roleRepository.save(role);
    }
}
