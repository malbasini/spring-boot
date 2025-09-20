package com.example.demo.mycourse.service;
import com.example.demo.mycourse.repository.LessonRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import com.example.demo.mycourse.model.Lesson;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;

    public LessonServiceImpl(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

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
    public Lesson findByTitleAndCourseId(String title, int courseId) {
        return lessonRepository.findByTitleAndCourseId(title, courseId);
    }
    @Override
    public void updateLesson(Lesson lesson) {
        lessonRepository.save(lesson);
    }
    @Override
    public void deleteLesson(int id) {
        lessonRepository.deleteById(id);
    }
}