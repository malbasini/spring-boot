package com.example.demo.mycourse.controller;

import com.example.demo.mycourse.service.CommonPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.security.Principal;

@Controller
@RequestMapping("/payment/paypal")
public class PayPalController {

    @Autowired
    private CommonPayService commonPayService;

    @GetMapping("/{courseId}/pay")
    public String payWithPayPal(@PathVariable("courseId") Integer courseId,
                                Principal principal,
                                Model model,
                                HttpSession session) {
        return commonPayService.payWithPayPal(courseId, principal, model, session);
    }

    @GetMapping("/success")
    public String successPayPal(@RequestParam("paymentId") String paymentId,
                                @RequestParam("PayerID") String payerId,
                                Model model,
                                HttpSession httpSession) {
        return commonPayService.successPay(paymentId, payerId, model, httpSession);
    }

    @GetMapping("/cancel")
    public String cancelPayPal(HttpSession httpSession) {
        return commonPayService.cancelPay();
    }
}
