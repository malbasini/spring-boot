package com.example.demo.service;

import com.example.demo.model.Course;
import com.example.demo.model.Subscription;
import com.example.demo.model.Register;

import java.math.BigDecimal;

public interface SubscriptionService {

    Subscription createSubscription(
            Integer userId,
            Integer courseId,
            BigDecimal amount,
            String currency,
            String paymentType,
            String transactionId,
            int vote);
    void subscriptionVote(int subscriptionId, int vote);

    void save(Subscription subscription);
}
