package com.example.demo.mycourse.controller;

import com.example.demo.mycourse.service.CommonPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.security.Principal;

/**
 * Controller classico Spring MVC (senza @RestController se si usano JSP).
 */
@Controller
@RequestMapping("/payment/stripe")
public class StripeController {

    private final CommonPayService commonPayService;

    public StripeController(CommonPayService commonPayService)
    {
        this.commonPayService = commonPayService;
    }

    @GetMapping("/{courseId}/pay")
    public String payWithStripe(@PathVariable("courseId") Integer courseId,
                                Principal principal,
                                Model model,
                                HttpSession session) {
        return commonPayService.startCheckout(courseId, principal, session, model);
    }

    @GetMapping("/success")
    public String successStripe(@RequestParam("session_id") String sessionId,
                                Model model,
                                HttpSession httpSession) {
        return commonPayService.success(sessionId, model, httpSession);
    }

    @GetMapping("/cancel")
    public String cancelStripe() {
        return commonPayService.cancel();
    }
}
