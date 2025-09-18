package com.example.demo.mycourse.repository;

import com.example.demo.mycourse.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import java.util.List;


public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO Courses (Title) VALUES (:title)", nativeQuery = true)
    int insertIgnore(@Param("title") String title);
    @Query("SELECT c FROM Course c WHERE c.title = :title")
    Course findByTitle(@Param("title") String title);
    Page<Course> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    // Corsi con la valutazione più alta (limite 3)
    @Query("SELECT c FROM Course c ORDER BY c.rating DESC")
    List<Course> findTopByRating(Pageable pageable);
    // Ultimi corsi aggiunti (limite 3)
    @Query("SELECT c FROM Course c ORDER BY c.id DESC")
    List<Course> findTopByNewest(Pageable pageable);
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.lessons WHERE c.id = :id")
    Course findCourseWithLessons(@Param("id") Integer id);
    boolean existsByTitle(String title);
    Course findCourseByAuthorAndId(String author, int courseId);
    Course findCourseById(Integer id);

    Course findById(int id);

    Course save(Course course);

    @Transactional
    @Modifying
    @Query("UPDATE Course c SET c.imagePath = :image WHERE c.id = :id")
    int updateImage(@Param("image") String image, @Param("id") int id);
    boolean existsByIdAndUserOwner_Username(int id, String username);
}


