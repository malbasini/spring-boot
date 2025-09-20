package com.example.demo.mycourse.service;

import com.example.demo.mycourse.model.Course;
import com.example.demo.mycourse.model.Subscription;
import com.example.demo.mycourse.model.User;
import com.example.demo.mycourse.repository.CourseRepository;
import com.example.demo.mycourse.repository.UserRepository;
import com.paypal.base.rest.PayPalRESTException;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.security.Principal;

@Service
public class CommonPayService {

    private final PayPalService payPalService;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final StripeService stripeService;

    public CommonPayService(PayPalService payPalService,
                            UserRepository userRepository,
                            CourseRepository courseRepository,
                            StripeService stripeService
                            ) {
        this.payPalService = payPalService;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.stripeService = stripeService;
    }

    // Esempio di rotta per avviare il pagamento
    public String payWithPayPal(@PathVariable("courseId") Integer courseId,
                                Principal principal,
                                Model model,
                                HttpSession session)
    {
        String loggedUsername = principal.getName();
        User user = userRepository.findByUsername(loggedUsername);
        Course course = courseRepository.findCourseById(courseId);
        int userId = user.getId();
        session.setAttribute("userId", userId);
        session.setAttribute("courseId", courseId);
        // Parametri fissi o recuperati da form
        BigDecimal amount = course.getCurrentPriceAmount();
        String currency = "EUR";
        String description = course.getTitle();
        String successUrl = "https://localhost:8443/payment/paypal/success";
        String cancelUrl = "https://localhost:8443/payment/paypal/cancel";
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
    public String successPay(@RequestParam("paymentId") String paymentId,
                             @RequestParam("PayerID") String payerId,
                             Model model,
                             HttpSession httpSession) {
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
    public String cancelPay() {
        // gestisci l'annullamento
        return "paypal/paymentcancelled";
    }

    public String startCheckout(@PathVariable("courseId") Integer courseId,
                                Principal principal,
                                HttpSession session,
                                Model model) {
        // Parametri fissi o recuperati da form/DB
        String loggedUsername = principal.getName();
        Integer userId = userRepository.findByUsername(loggedUsername).getId();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));
        session.setAttribute("userId", userId);
        session.setAttribute("courseId", courseId);
        BigDecimal amount = new BigDecimal(String.valueOf(course.getCurrentPriceAmount().doubleValue()));
        String currency = "EUR";
        String successUrl = "https://localhost:8443/payment/stripe/success";
        String cancelUrl = "https://localhost:8443/payment/stripe/cancel";
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

    public String cancel() {
        return "stripe/paymentcancelled";
    }
}
