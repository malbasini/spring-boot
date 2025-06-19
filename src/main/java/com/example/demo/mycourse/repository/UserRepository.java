package com.example.demo.mycourse.repository;

import com.example.demo.mycourse.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<Object> findUserById(Integer userId);

    User findByEmail(String email);
}