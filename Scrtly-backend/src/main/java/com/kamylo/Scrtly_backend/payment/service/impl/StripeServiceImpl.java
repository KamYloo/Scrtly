package com.kamylo.Scrtly_backend.payment.service.impl;

import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.payment.config.StripeConfig;
import com.kamylo.Scrtly_backend.payment.domain.entity.SubscriptionEntity;
import com.kamylo.Scrtly_backend.payment.domain.enums.SubscriptionStatus;
import com.kamylo.Scrtly_backend.payment.repository.SubscriptionRepository;
import com.kamylo.Scrtly_backend.payment.service.StripeCustomerService;
import com.kamylo.Scrtly_backend.payment.service.StripeService;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.service.UserService;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {
    private final StripeConfig cfg;
    private final UserService userService;
    private final StripeCustomerService customerService;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public String createCheckoutSession(String username, String successUrl, String cancelUrl) {
        UserEntity user = userService.findUserByEmail(username);
        List<SubscriptionEntity> activeSubs = subscriptionRepository.findAllByUserAndStatusIn(
                user, List.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.PENDING)
        );

        if (!activeSubs.isEmpty()) {
            throw new CustomException(BusinessErrorCodes.USER_ALREADY_SUBSCRIBED);
        }

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

        RequestOptions requestOptions = RequestOptions.builder()
                .setIdempotencyKey("checkout_session_user_" + user.getId() + "_" + System.currentTimeMillis())
                .build();

        try {
            Session session = Session.create(params, requestOptions);
            return session.getId();
        } catch (StripeException e) {
            throw new CustomException(BusinessErrorCodes.STRIPE_API_ERROR, e);
        }
    }

    @Override
    public Event constructEvent(String payload, String sigHeader) {
        try {
            return Webhook.constructEvent(payload, sigHeader, cfg.getWebhookSecret());
        } catch (SignatureVerificationException ex) {
            throw new CustomException(BusinessErrorCodes.STRIPE_WEBHOOK_SIGNATURE_INVALID, ex);
        }
    }

    @Override
    public Subscription cancelSubscription(String subscriptionId) {
        try {
            Subscription subscription = Subscription.retrieve(subscriptionId);
            return subscription.cancel();
        } catch (StripeException e) {
            throw new CustomException(BusinessErrorCodes.STRIPE_API_ERROR, e);
        }
    }

    @Override
    public String createBillingPortalSession(String username) {
        UserEntity user = userService.findUserByEmail(username);

        com.stripe.param.billingportal.SessionCreateParams params = com.stripe.param.billingportal.SessionCreateParams.builder()
                .setCustomer(user.getStripeCustomerId())
                .setReturnUrl(cfg.getPortalReturnUrl())
                .build();
        try {
            com.stripe.model.billingportal.Session session = com.stripe.model.billingportal.Session.create(params);
            return session.getUrl();
        } catch (StripeException e) {
            throw new CustomException(BusinessErrorCodes.STRIPE_API_ERROR, e);
        }
    }
}