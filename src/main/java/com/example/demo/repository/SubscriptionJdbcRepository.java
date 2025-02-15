package com.example.demo.repository;

import com.example.demo.model.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;

@Repository
public class SubscriptionJdbcRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public int saveSubscription(Subscription subscription) {
        try {
            String sql = "INSERT INTO Subscription (UserId, CourseId, PaymentDate, PaidAmount, PaidCurrency, PaymentType, TransactionId, Vote) VALUES (?,?,?,?,?,?,?,?)";
            jdbcTemplate.update(sql, subscription.getUser().getId(), subscription.getCourse().getId(), subscription.getPaymentDate(), subscription.getPaidAmount(), subscription.getPaidCurrency(), subscription.getPaymentType(),subscription.getTransactionId(),subscription.getVote());
            Integer subscriptionId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
            return subscriptionId;
        }catch (Exception e) {
            throw e;
        }
    }
    @Transactional
    public int updateVote(int subscriptionId, int vote) {
        String sql = "UPDATE Subscription SET Vote = ? WHERE Id = ?";
        return jdbcTemplate.update(sql, vote, subscriptionId);
    }
}
