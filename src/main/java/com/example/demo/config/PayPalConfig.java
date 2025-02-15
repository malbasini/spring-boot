package com.example.demo.config;

import com.paypal.base.rest.APIContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")

public class PayPalConfig {
    // Recuperate da application.properties o da variabili d'ambiente
    @Value("${paypal.client.id}")
    private String clientId;
    @Value("${paypal.client.secret}")
    private String clientSecret;
    @Value("${paypal.mode}")
    private String mode; // "sandbox" o "live"

    @Bean
    public APIContext apiContext() {
        APIContext context = new APIContext(getClientId(), getClientSecret(), getMode());
        return context;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getMode() {
        return mode;
    }

}