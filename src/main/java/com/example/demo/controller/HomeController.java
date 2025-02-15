package com.example.demo.controller;

import com.example.demo.model.Course;
import com.example.demo.service.CoursesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private CoursesService courseService;

    @GetMapping("/")
    public String ShowHome(Model model) {
        // Recupera i corsi
        List<Course> topRatedCourses = courseService.getTopRatedCourses();
        List<Course> newestCourses = courseService.getNewestCourses();
        // Aggiunge i dati al modello
        model.addAttribute("topRatedCourses", topRatedCourses);
        model.addAttribute("newestCourses", newestCourses);
        return "index";
    }

}
