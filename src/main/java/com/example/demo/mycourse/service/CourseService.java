package com.example.demo.mycourse.service;
import java.util.List;
import com.example.demo.mycourse.model.Course;
import com.example.demo.mycourse.model.User;
import org.springframework.data.domain.Page;

public interface CourseService {
    Iterable<Course> findAll();
    Course findById(Integer id);
    void deleteById(Integer id);
    Page<Course> findCourses(int page, int size, String title, String sortBy, String sortDirection);
    List<Course> getTopRatedCourses();
    List<Course> getNewestCourses();
    Course saveCourse(Course course);
    Course getCourseByIdWithLessons(Integer id);
    Course updateCourse(Course course);
    void deleteCourse(int id);
    void updateImagePath(String image, int id);
    User findByUsername(String username);
    String getEmailByCourseIdAndAuthor(int courseId, String author);
}
