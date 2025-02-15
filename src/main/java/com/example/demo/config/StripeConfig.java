package com.example.demo.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import jakarta.annotation.PostConstruct;
@Configuration
@PropertySource("classpath:application.properties")

public class StripeConfig {
    @Value("${stripe.secret.key}")
    private String secretKey;
    @PostConstruct
    public void init() {
        // Imposta la chiave privata Stripe a livello globale
        Stripe.apiKey = getSecretKey();
    }

    public String getSecretKey() {
        return secretKey;
    }
}