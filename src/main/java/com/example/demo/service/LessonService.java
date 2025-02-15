package com.example.demo.service;
import java.util.List;
import java.util.Optional;

import com.example.demo.model.*;

public interface LessonService {
    List<Lesson> findByCourseId(Integer courseId);
    Lesson findById(Integer id);
    Lesson save(Lesson lesson);
    void deleteById(Integer id);
    Lesson findByTitleAndCourseId(String title, int courseId);
}