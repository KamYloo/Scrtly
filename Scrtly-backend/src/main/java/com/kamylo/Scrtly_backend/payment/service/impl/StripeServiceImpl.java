package com.kamylo.Scrtly_backend.payment.service.impl;

import com.kamylo.Scrtly_backend.payment.config.StripeConfig;
import com.kamylo.Scrtly_backend.payment.service.StripeCustomerService;
import com.kamylo.Scrtly_backend.payment.service.StripeService;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.service.UserService;
import com.stripe.model.Subscription;
import com.stripe.net.RequestOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;

@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {
    private final StripeConfig cfg;
    private final UserService userService;
    private final StripeCustomerService customerService;

    @Override
    public Session createCheckoutSession(String username, String successUrl, String cancelUrl) throws StripeException {
        UserEntity user = userService.findUserByEmail(username);
        customerService.ensureCustomer(user);
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setCustomer(user.getStripeCustomerId())
                .setClientReferenceId(user.getId().toString())
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPrice(cfg.getPriceIdMonthly())
                        .setQuantity(1L)
                        .build())
                .build();
        String idempotencyKey = "checkout_session_user_" + user.getId() + "_" + System.currentTimeMillis();
        RequestOptions requestOptions = RequestOptions.builder()
                .setIdempotencyKey(idempotencyKey)
                .build();

        return Session.create(params, requestOptions);
    }

    @Override
    public Event constructEvent(String payload, String sigHeader) throws SignatureVerificationException {
        return Webhook.constructEvent(payload, sigHeader, cfg.getWebhookSecret());
    }

    @Override
    public Subscription cancelSubscription(String subscriptionId) throws StripeException {
        Subscription subscription = Subscription.retrieve(subscriptionId);
        return subscription.cancel();
    }
}