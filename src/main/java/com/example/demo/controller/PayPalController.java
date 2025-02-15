package com.example.demo.controller;

import com.example.demo.model.Course;
import com.example.demo.model.Subscription;
import com.example.demo.model.Register;
import jakarta.servlet.http.HttpSession;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.PayPalService;
import com.example.demo.service.UserService;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.security.Principal;

/**
 * Controller classico Spring MVC (senza @RestController se si usano JSP).
 */
@Controller
@RequestMapping("/paypal")
public class PayPalController {
    @Autowired
    private PayPalService payPalService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    // Esempio di rotta per avviare il pagamento
    @GetMapping("/{courseId}/pay")
    public String payWithPayPal(@PathVariable("courseId") Integer courseId,
                                Principal principal,
                                Model model,
                                HttpSession session)
     {
        String loggedUsername = principal.getName();
        Register user = userRepository.findByUsername(loggedUsername);
        Course course = courseRepository.findCourseById(courseId);
        int userId = user.getId();
        session.setAttribute("userId", userId);
        session.setAttribute("courseId", courseId);
         // Parametri fissi o recuperati da form
        BigDecimal amount = course.getCurrentPriceAmount();
        String currency = "EUR";
        String description = course.getTitle();
        String successUrl = "http://localhost:8080/paypal/success";
        String cancelUrl = "http://localhost:8080/paypal/cancel";
        try {
                String approvalLink = payPalService.createPayment(
                    user,
                    course,
                    amount,
                    currency,
                    description,
                    successUrl,
                    cancelUrl
                );
                // redirect su PayPal
                return "redirect:" + approvalLink;
        } catch (PayPalRESTException e) {
            e.printStackTrace();
            // Gestione errore
            model.addAttribute("message", e.getMessage());
            return "paypal/error";
        }
    }
    // Rotta di successo definita come returnUrl in createPayment
    @GetMapping("/success")
    public String successPay(@RequestParam("paymentId") String paymentId,
                             @RequestParam("PayerID") String payerId, Model model, HttpSession httpSession) {
        try {
            Subscription subscription = payPalService.executePayment(paymentId, payerId, httpSession);
            if (subscription != null) {
                // Pagamento completato con successo:
                // mostra una pagina di conferma, ad es. "paymentSuccess.jsp"
                model.addAttribute("subscription", subscription);
                return "paypal/paymentsuccess";
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
            model.addAttribute("message", e.getMessage());
        }
        return "paypal/error";
    }
    // Rotta di annullamento definita come cancelUrl
    @GetMapping("/cancel")
    public String cancelPay() {
        // gestisci l'annullamento
        return "paypal/paymentcancelled";
    }
}