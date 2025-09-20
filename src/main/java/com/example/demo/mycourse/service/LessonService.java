package com.example.demo.mycourse.service;
import com.example.demo.mycourse.model.Lesson;
import java.util.List;

public interface LessonService {

    List<Lesson> findByCourseId(Integer courseId);
    Lesson findById(Integer id);
    Lesson save(Lesson lesson);
    Lesson findByTitleAndCourseId(String title, int courseId);
    void updateLesson(Lesson lesson);
    void deleteLesson(int id);

}