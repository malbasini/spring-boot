package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.example.demo.model.Lesson;

public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    // Esempio: recupera tutte le lessons di un certo courseId
    List<Lesson> findByCourseId(Integer courseId);
    boolean existsByTitleAndCourseId(String title,int courseId);
    Lesson findByTitleAndCourseId(String title,int courseId);

    Lesson findLessonsById(Integer id);
}