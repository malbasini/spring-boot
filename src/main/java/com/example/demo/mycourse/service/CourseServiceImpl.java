package com.example.demo.mycourse.service;
import com.example.demo.mycourse.model.Course;
import com.example.demo.mycourse.model.User;
import com.example.demo.mycourse.repository.CourseRepository;
import com.example.demo.mycourse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Iterable<Course> findAll() {
        return courseRepository.findAll();
    }
    @Override
    public Course findById(Integer id) {
        return courseRepository.findCourseById(id);
    }
    @Transactional
    @Override
    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }
    @Override
    public Course getCourseByIdWithLessons(Integer id) {
        return courseRepository.findCourseWithLessons(id);
    }

    @Override
    public void deleteById(Integer id) {
        courseRepository.deleteById(id);
    }


    public Page<Course> findCourses(int page, int size, String title, String sortBy, String sortDirection) {
        Sort sort = Sort.by(sortBy);
        sort = sortDirection.equalsIgnoreCase("desc") ? sort.descending() : sort.ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        // Filtro per titolo
        if (title != null && !title.isEmpty()) {
            return courseRepository.findByTitleContainingIgnoreCase(title, pageable);
        }

        return courseRepository.findAll(pageable);
    }
    public List<Course> getTopRatedCourses() {
        Pageable pageable = PageRequest.of(0, 3); // Prima pagina, 3 risultati
        return courseRepository.findTopByRating(pageable);
    }

    public List<Course> getNewestCourses() {
        Pageable pageable = PageRequest.of(0, 3); // Prima pagina, 3 risultati
        return courseRepository.findTopByNewest(pageable);
    }
    public Course updateCourse(Course course) {
        return courseRepository.save(course);
    }
    public void deleteCourse(int id) {
        courseRepository.deleteById(id);
    }
    public void updateImagePath(String image, int id) {
        courseRepository.updateImage(image,id);
    }
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public String getEmailByCourseIdAndAuthor(int courseId, String author) {
        return courseRepository.findCourseByAuthorAndId(author, courseId).getEmail();
    }
}