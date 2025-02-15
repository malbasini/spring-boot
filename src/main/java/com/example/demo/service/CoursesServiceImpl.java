package com.example.demo.service;

import com.example.demo.model.Course;
import com.example.demo.model.Register;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class CoursesServiceImpl implements CoursesService {
    @Autowired
    private CourseRepository coursesRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Course> findAll() {
        return coursesRepository.findAll();
    }

    @Override
    public Course findById(Integer id) {
        Optional<Course> optional = coursesRepository.findById(id);
        return optional.orElse(null);
    }
    public Page<Course> findCourses(int page, int size, String title, String sortBy, String sortDirection) {
        Sort sort = Sort.by(sortBy);
        sort = sortDirection.equalsIgnoreCase("desc") ? sort.descending() : sort.ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        // Filtro per titolo
        if (title != null && !title.isEmpty()) {
            return coursesRepository.findByTitleContainingIgnoreCase(title, pageable);
        }
        return coursesRepository.findAll(pageable);
    }
    public List<Course> getTopRatedCourses() {
        Pageable pageable = PageRequest.of(0, 3); // Prima pagina, 3 risultati
        return coursesRepository.findTopByRating(pageable);
    }

    public List<Course> getNewestCourses() {
        Pageable pageable = PageRequest.of(0, 3); // Prima pagina, 3 risultati
        return coursesRepository.findTopByNewest(pageable);
    }

    @Override
    public Course getCourseByIdWithLessons(Integer id) {
        return coursesRepository.findCourseById(id);
    }


    @Override
    public Course getEmailByCourseIdAndAuthor(int courseId, String author) {
        return coursesRepository.findByIdAndAuthor(courseId,author);
    }

    @Override
    public Register findByUsername(String username) {
        Register user = userRepository.findByUsername(username);
        return user;
    }

    @Override
    public void deleteCourse(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public Course save(Course course) {
        return coursesRepository.save(course);
    }

    @Override
    public void update(Course updatedCourse) {
        coursesRepository.save(updatedCourse);
    }

    @Override
    public void delete(Integer id) {
        coursesRepository.deleteById(id);
    }
}