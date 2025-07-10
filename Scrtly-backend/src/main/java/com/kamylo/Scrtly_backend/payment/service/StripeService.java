package com.kamylo.Scrtly_backend.payment.service;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;

public interface StripeService {
    Session createCheckoutSession(String username, String successUrl, String cancelUrl) throws StripeException;
    Event constructEvent(String payload, String sigHeader) throws SignatureVerificationException;
    Subscription cancelSubscription(String subscriptionId) throws StripeException;
}
