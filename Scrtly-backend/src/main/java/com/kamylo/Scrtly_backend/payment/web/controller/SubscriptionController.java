package com.kamylo.Scrtly_backend.payment.web.controller;

import com.kamylo.Scrtly_backend.payment.web.dto.CreateSubscriptionRequest;
import com.kamylo.Scrtly_backend.payment.service.StripeService;
import com.kamylo.Scrtly_backend.payment.service.SubscriptionService;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {
    private final StripeService stripe;
    private final SubscriptionService subscriptionService;
    private final UserRepository userRepo;

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> create(
            Principal principal,
            @Valid @RequestBody CreateSubscriptionRequest req) {

        Session session = stripe.createCheckoutSession(principal.getName(), req.getSuccessUrl(), req.getCancelUrl());
        return ResponseEntity.ok(Map.of("sessionId", session.getId()));
    }

    @PostMapping("/{subscriptionId}/cancel")
    public ResponseEntity<Void> cancelSubscription(
            Principal principal,
            @PathVariable String subscriptionId) {

        UserEntity user = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean belongsToUser = user.getSubscriptions().stream()
                .anyMatch(s -> subscriptionId.equals(s.getStripeSubscriptionId()));
        if (!belongsToUser) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        stripe.cancelSubscription(subscriptionId);
        subscriptionService.cancelLocalSubscription(subscriptionId);

        return ResponseEntity.ok().build();
    }

}