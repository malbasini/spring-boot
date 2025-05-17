package com.example.demo.mycourse.service;
import com.example.demo.mycourse.model.Course;
import com.example.demo.mycourse.model.Subscription;
import com.example.demo.mycourse.model.User;
import com.example.demo.mycourse.repository.SubscriptionRepository;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;

@Service
public class PayPalService {
    @Autowired
    private APIContext apiContext; // Iniettato da PayPalConfig
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    /**
     * Crea il pagamento su PayPal e restituisce l'approvalLink per il redirect.
     */
    public String createPayment(
            User user,
            Course course,
            BigDecimal amount,
            String currency,
            String description,
            String returnUrl,
            String cancelUrl) throws PayPalRESTException {
        // 1) Importo
        Amount payAmount = new Amount();
        payAmount.setCurrency(currency);
        payAmount.setTotal(String.valueOf(amount));
        // 2) Transaction
        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(payAmount);
        // 3) Payment
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");
        Payment payment = new Payment();
        payment.setIntent("sale"); // o "authorize", dipende dal tuo flusso
        payment.setPayer(payer);
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(returnUrl);
        payment.setRedirectUrls(redirectUrls);
        payment.setTransactions(java.util.Arrays.asList(transaction));
        // 4) Crea il pagamento su PayPal
        Payment createdPayment = payment.create(apiContext);
        // 5) Estrai il link di approvazione
        for (Links link : createdPayment.getLinks()) {
            if (link.getRel().equalsIgnoreCase("approval_url")) {
                return link.getHref();
            }
        }
        return null; // in caso non trovi link
    }

    /**
     * Esegue il pagamento dopo il redirect di successo da PayPal
     */
    public Subscription executePayment(String paymentId, String payerId, HttpSession session) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);
        Payment executedPayment = payment.execute(apiContext, paymentExecution);
        if ("approved".equalsIgnoreCase(executedPayment.getState())) {
            // Esempio: recuperiamo i primi dati dalla transazione
            Transaction transaction = executedPayment.getTransactions().get(0);
            Amount amount = transaction.getAmount();
            String currency = amount.getCurrency();
            BigDecimal paidAmount = new BigDecimal(amount.getTotal());
            // Salviamo la Subscription.
            // Nel tuo caso devi sapere userId/courseId,
            // potresti recuperarli dalla sessione o da DB prima del redirect.
            // Qui facciamo un esempio statico:
            // in realtà recuperalo da sessione/param/etc.
            int userId = (int) session.getAttribute("userId");
            int courseId = (int) session.getAttribute("courseId");
            if (session.getAttribute("userId") == null || session.getAttribute("courseId") == null) {
                throw new IllegalStateException("I dettagli della sessione non sono stati trovati.");
            }
            Subscription subscription = subscriptionService.createSubscription(userId, courseId, paidAmount, currency, "PAYPAL", executedPayment.getId());
            return subscription;
        }
        return null; // se non approved
    }
}
