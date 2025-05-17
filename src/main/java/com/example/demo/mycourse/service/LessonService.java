package com.example.demo.mycourse.service;
import com.example.demo.mycourse.model.Lesson;

import java.util.List;

public interface LessonService {
    List<Lesson> findByCourseId(Integer courseId);
    Lesson findById(Integer id);
    Lesson save(Lesson lesson);
    void deleteById(Integer id);
    Lesson findByTitleAndCourseId(String title, int courseId);
    Lesson updateLesson(Lesson lesson);
    void deleteLesson(int id);

}