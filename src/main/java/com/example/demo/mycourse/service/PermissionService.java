package com.example.demo.mycourse.service;

import com.example.demo.mycourse.repository.CourseRepository;
import org.springframework.stereotype.Component;

@Component("permission")
public class PermissionService {
    private final CourseRepository courseRepo;
    public PermissionService(CourseRepository courseRepo) { this.courseRepo = courseRepo; }

    public boolean isOwnerOfCourse(int courseId, org.springframework.security.core.Authentication auth) {
        return courseRepo.existsByIdAndUserOwner_Username(courseId, auth.getName());
    }
}