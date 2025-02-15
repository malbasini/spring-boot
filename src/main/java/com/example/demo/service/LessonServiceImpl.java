package com.example.demo.service;
import com.example.demo.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import com.example.demo.model.Lesson;

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

    public Lesson findByTitleAndCourseId(String title, int courseId) {
        return lessonRepository.findByTitleAndCourseId(title, courseId);
    }

}