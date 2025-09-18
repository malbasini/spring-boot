package com.example.demo.mycourse.service;

import com.example.demo.mycourse.repository.CourseRepository;
import com.example.demo.mycourse.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("permission")
public class PermissionService {

    private final CourseRepository courseRepo;
    private final UserRepository userRepository;
    public PermissionService(CourseRepository courseRepo, UserRepository userRepository) {
        this.courseRepo = courseRepo;
        this.userRepository = userRepository;
    }

    /** true se l'utente autenticato è l'owner del corso */
    public boolean isOwnerOfCourse(int courseId, Authentication auth) {
        if (auth == null || auth.getName() == null) return false;
        return courseRepo.existsByIdAndUserOwner_Username(courseId, auth.getName());
        // In alternativa:
        // return courseRepo.isOwner(courseId, auth.getName());
    }
    public boolean userHasRole(String username, String role) {
        return userRepository.existsByUsernameAndRoles_Name(username, role);
    }
}