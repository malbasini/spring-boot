package com.example.demo.mycourse.repository;

import com.example.demo.mycourse.model.Subscription;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    Subscription findByCourse_Id(Integer id);
    Subscription findByUser_Id(Integer id);
    Subscription findByCourse_IdAndUser_Id(int courseId, Integer userId);

    @Transactional
    @Modifying
    @Query("UPDATE Subscription s SET s.vote = :vote WHERE s.id = :id")
    int updateVote(@Param("vote") int vote, @Param("id") int id);

}