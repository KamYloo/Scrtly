package com.kamylo.Scrtly_backend.payment.service;

import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.stripe.model.Invoice;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;

public interface SubscriptionService {
    void handleCheckoutSession(Session session, Long userId);
    void handleInvoicePaymentSucceeded(Invoice invoice);
    void handleSubscriptionDeleted(Subscription subscription);
    void handleInvoicePaymentFailed(Invoice invoice);
    void cancelLocalSubscription(String subscriptionId);
}
