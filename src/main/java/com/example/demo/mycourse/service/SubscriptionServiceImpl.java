package com.example.demo.mycourse.service;

import com.example.demo.mycourse.model.Course;
import com.example.demo.mycourse.model.Subscription;
import com.example.demo.mycourse.model.User;
import com.example.demo.mycourse.repository.CourseRepository;
import com.example.demo.mycourse.repository.SubscriptionRepository;
import com.example.demo.mycourse.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Date;

@Service
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public SubscriptionServiceImpl(CourseRepository courseRepository,
                                   UserRepository userRepository,
                                   SubscriptionRepository subscriptionRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;

    }

    /**
     * Crea (o aggiorna) l'iscrizione dopo il pagamento avvenuto.
     *
     * @param userId        lo studente
     * @param courseId      il corso
     * @param amount        importo pagato
     * @param currency      valuta
     * @param paymentType   "PAYPAL" o "STRIPE"
     * @param transactionId l'ID transazione restituito dal gateway
     * @return l'oggetto Subscription salvato
     */

    public Subscription createSubscription(
            Integer userId,
            Integer courseId,
            BigDecimal amount,
            String currency,
            String paymentType,
            String transactionId) {
        Subscription subscription = new Subscription();
        User user = userRepository.findById(userId).orElse(null);
        subscription.setUser(user);
        Course course = courseRepository.findCourseById(courseId);
        subscription.setCourse(course);
        subscription.setPaymentDate(new Date());
        subscription.setPaidAmount(amount);
        subscription.setPaidCurrency(currency);
        subscription.setPaymentType(paymentType);
        subscription.setTransactionId(transactionId);
        subscription.setVote(1);
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        return savedSubscription;
    }

    @Override
    public void subscriptionVote(int subscriptionId, int vote) {
          subscriptionRepository.updateVote(vote,subscriptionId);
    }
}
