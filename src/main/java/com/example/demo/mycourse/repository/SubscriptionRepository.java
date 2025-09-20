package com.example.demo.mycourse.repository;

import com.example.demo.mycourse.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

    Subscription findByCourse_Id(Integer id);
    @Query("UPDATE Subscription s SET s.vote = :vote WHERE s.id = :id")
    int updateVote(@Param("vote") int vote, @Param("id") int id);

}