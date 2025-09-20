package com.example.demo.mycourse.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@ConfigurationProperties(prefix = "mail.trap.key")
public class MailConfig {

    @Value("${mail.trap.key.user}")
    private String user;
    @Value("${mail.trap.key.password}")
    private String password;
    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        // Imposta i valori per Mailtrap
        mailSender.setHost("sandbox.smtp.mailtrap.io");
        mailSender.setPort(2525);
        mailSender.setUsername(getUser());
        mailSender.setPassword(getPassword());
        return mailSender;
    }

    public String getUser() {
        return user;
    }
    public String getPassword() {
        return password;
    }

}
