package com.example.demo.mycourse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        try {
            message.setFrom("no-reply@localhost");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
        }
        catch (Exception e) {
            throw e;
        }

        mailSender.send(message);
    }
}