package com.example.demo.service;

import com.example.demo.model.Course;
import com.example.demo.model.Register;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CoursesService {
    List<Course> findAll();
    Course findById(Integer id);
    Page<Course> findCourses(int page, int size, String title, String sortBy, String sortDirection);
    List<Course> getTopRatedCourses();
    List<Course> getNewestCourses();
    Course getCourseByIdWithLessons(Integer id);
    Course getEmailByCourseIdAndAuthor(int courseId, String author);


    Register findByUsername(String username);

    void deleteCourse(Integer id);

    Course save(@Valid Course course);

    void update(@Valid Course updatedCourse);

    void delete(Integer id);
}