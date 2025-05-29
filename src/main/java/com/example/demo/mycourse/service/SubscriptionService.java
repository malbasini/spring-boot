package com.example.demo.mycourse.service;
import com.example.demo.mycourse.model.Subscription;
import java.math.BigDecimal;

public interface SubscriptionService {

    Subscription createSubscription(
            Integer userId,
            Integer courseId,
            BigDecimal amount,
            String currency,
            String paymentType,
            String transactionId);
    void subscriptionVote(int subscriptionId, int vote);
}
