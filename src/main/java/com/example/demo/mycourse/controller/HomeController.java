package com.example.demo.mycourse.controller;
import com.example.demo.mycourse.model.Course;
import com.example.demo.mycourse.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class HomeController {

    private final CourseService courseService;

    public HomeController(CourseService courseService) {
        this.courseService = courseService;
    }
    @GetMapping("/")
    public String showHomePage(Model model) {
        // Recupera i corsi
        List<Course> topRatedCourses = courseService.getTopRatedCourses();
        List<Course> newestCourses = courseService.getNewestCourses();

        // Aggiunge i dati al modello
        model.addAttribute("topRatedCourses", topRatedCourses);
        model.addAttribute("newestCourses", newestCourses);

        // Ritorna la vista JSP
        return "home";
    }
}
