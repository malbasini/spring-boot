package com.example.demo.repository;

import com.example.demo.model.Register;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Register, Integer> {
    Register findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query("SELECT r FROM Register r LEFT JOIN FETCH r.roles WHERE r.username = :username")
    Optional<Register> findByUsernameWithRoles(@Param("username") String username);
}