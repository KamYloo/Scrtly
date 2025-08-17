package com.kamylo.Scrtly_backend.payment.service;

import com.stripe.model.Event;
import com.stripe.model.Subscription;

public interface StripeService {
    String createCheckoutSession(String username, String successUrl, String cancelUrl);
    Event constructEvent(String payload, String sigHeader);
    Subscription cancelSubscription(String subscriptionId);
    String createBillingPortalSession(String username);
}
