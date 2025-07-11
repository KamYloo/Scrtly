package com.kamylo.Scrtly_backend.payment.service.impl;

import com.kamylo.Scrtly_backend.payment.domain.entity.SubscriptionEntity;
import com.kamylo.Scrtly_backend.payment.repository.SubscriptionRepository;
import com.kamylo.Scrtly_backend.payment.domain.enums.SubscriptionStatus;
import com.kamylo.Scrtly_backend.payment.service.SubscriptionService;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.service.UserService;
import com.stripe.model.Invoice;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository repo;
    private final UserService userService;

    @Override
    @Transactional
    public void handleCheckoutSession(Session session, Long userId) {
        UserEntity user = userService.findUserById(userId);

        String stripeSubId = session.getSubscription();
        if (repo.findByStripeSubscriptionId(stripeSubId).isPresent()) {
            return;
        }

        SubscriptionEntity e = SubscriptionEntity.builder()
                .user(user)
                .stripeSubscriptionId(stripeSubId)
                .startDate(LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(session.getCreated()),
                        ZoneOffset.UTC))
                .status(SubscriptionStatus.PENDING)
                .build();
        repo.save(e);
    }

    @Override
    @Transactional
    public void handleInvoicePaymentSucceeded(Invoice invoice) {
        String subscriptionId = extractSubscriptionId(invoice);
        if (subscriptionId == null) {
            return;
        }
        repo.findByStripeSubscriptionId(subscriptionId)
                .ifPresent(e -> {
                    LocalDateTime newEnd = LocalDateTime.ofInstant(
                            Instant.ofEpochSecond(
                                    invoice.getLines()
                                            .getData().get(0)
                                            .getPeriod().getEnd()
                            ),
                            ZoneOffset.UTC);

                    if (e.getCurrentPeriodEnd() == null
                            || newEnd.isAfter(e.getCurrentPeriodEnd())) {
                        e.setCurrentPeriodEnd(newEnd);
                        e.setStatus(SubscriptionStatus.ACTIVE);
                        repo.save(e);
                    }
                });
    }

    @Override
    @Transactional
    public void handleSubscriptionDeleted(Subscription subscription) {
        repo.findByStripeSubscriptionId(subscription.getId())
                .ifPresent(e -> {
                    if (e.getStatus() != SubscriptionStatus.CANCELED) {
                        e.setStatus(SubscriptionStatus.CANCELED);
                        repo.save(e);
                    }
                });
    }

    @Override
    @Transactional
    public void handleInvoicePaymentFailed(Invoice invoice) {
        String subscriptionId = extractSubscriptionId(invoice);
        if (subscriptionId == null) {
            return;
        }
        repo.findByStripeSubscriptionId(subscriptionId).ifPresent(e -> {
            e.setStatus(SubscriptionStatus.PAST_DUE);
            repo.save(e);
        });
    }

    @Override
    @Transactional
    public void cancelLocalSubscription(String subscriptionId) {
        repo.findByStripeSubscriptionId(subscriptionId)
                .ifPresent(e -> {
                    e.setStatus(SubscriptionStatus.CANCELED);
                    repo.save(e);
                });
    }

    private String extractSubscriptionId(Invoice invoice) {
        if (invoice.getParent() != null
                && invoice.getParent().getSubscriptionDetails() != null) {
            return invoice.getParent()
                    .getSubscriptionDetails()
                    .getSubscription();
        }
        return null;
    }

}
