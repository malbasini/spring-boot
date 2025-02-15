package com.example.demo.controller;

import com.example.demo.model.Course;
import com.example.demo.model.Subscription;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CoursesService;
import com.example.demo.service.StripeService;
import com.stripe.exception.StripeException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.security.Principal;

@Controller
@RequestMapping("/stripe")
public class StripeController {
    @Autowired
    private StripeService stripeService;
    @Autowired
    private CoursesService courseService;
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/{courseId}/checkout")
    public String startCheckout(@PathVariable("courseId") Integer courseId,
                                Principal principal,
                                HttpSession session,
                                Model model) {
        // Parametri fissi o recuperati da form/DB
        String loggedUsername = principal.getName();
        Integer userId = userRepository.findByUsername(loggedUsername).getId();
        Course course = courseService.findById(courseId);
        session.setAttribute("userId", userId);
        session.setAttribute("courseId", courseId);
        BigDecimal amount = new BigDecimal(String.valueOf(course.getCurrentPriceAmount().doubleValue()));
        String currency = "EUR";
        String successUrl = "http://localhost:8080/spring-mvc-demo/stripe/success";
        String cancelUrl = "http://localhost:8080/spring-mvc-demo/stripe/cancel";
        try {
            String checkoutUrl = String.valueOf(stripeService.createCheckoutSession(userId, courseId, amount, currency, successUrl, cancelUrl
            ));
            // reindirizza lo user alla pagina di pagamento ospitata da Stripe
            return "redirect:" + checkoutUrl;
        } catch (StripeException e) {
            model.addAttribute("message", e.getMessage());
            return "stripe/error";
        }
    }
    @GetMapping("/success")
    public String success(@RequestParam("session_id") String sessionId,
                          Model model,
                          HttpSession httpSession) {
        try {
            Subscription subscription = stripeService.handleCheckoutSession(sessionId,httpSession);
            if (subscription != null) {
                model.addAttribute("subscription", subscription);
                return "stripe/paymentsuccess";
            }
        } catch (StripeException e) {
            model.addAttribute("message", e.getMessage());
        }
        return "stripe/error";
    }
    @GetMapping("/cancel")
    public String cancel() {
       //Gestisce il pagamento annullato
        return "stripe/paymentcancelled";
    }
/*
 * Esempio (opzionale) di endpoint Webhook se vuoi gestire in automatico
 * la conferma del pagamento Stripe, invece di passare da /success.
 */
// @PostMapping("/webhook")
// public ResponseEntity<String> handleStripeWebhook(HttpServletRequestrequest) {
// // Leggi e valida l'evento (Stripe-Signature) e aggiorna DB diconseguenza
// return ResponseEntity.ok("ok");
// }
}