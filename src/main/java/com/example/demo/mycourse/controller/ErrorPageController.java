package com.example.demo.mycourse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorPageController {

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "security/access-denied"; // la tua JSP/Thymeleaf esistente
    }
}