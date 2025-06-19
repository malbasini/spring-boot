package com.example.demo.mycourse.service;
import com.example.demo.mycourse.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


import com.example.demo.mycourse.model.Lesson;

@Service
public class LessonServiceImpl implements LessonService {
    @Autowired
    private LessonRepository lessonRepository;
    @Override
    public List<Lesson> findByCourseId(Integer courseId) {
        return lessonRepository.findByCourseId(courseId);
    }
    @Override
    public Lesson findById(Integer id) {
        return lessonRepository.findLessonsById(id);
    }
    @Override
    public Lesson save(Lesson lesson) {
        return lessonRepository.save(lesson);
    }
    @Override
    public void deleteById(Integer id) {
        lessonRepository.deleteById(id);
    }

    public Lesson saveLesson(Lesson lesson) {
        return lessonRepository.save(lesson);
    }
    public Lesson findByTitleAndCourseId(String title, int courseId) {
        return lessonRepository.findByTitleAndCourseId(title, courseId);
    }
    public Lesson updateLesson(Lesson lesson) {
        return lessonRepository.save(lesson);
    }
    public void deleteLesson(int id) {
        lessonRepository.deleteById(id);
    }
}