package com.example.demo.mycourse.repository;

import com.example.demo.mycourse.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);
    Optional<Role> findById(Integer id);
}