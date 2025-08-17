package com.kamylo.Scrtly_backend.payment.web.controller;

import com.kamylo.Scrtly_backend.payment.web.dto.SubscriptionDto;
import com.kamylo.Scrtly_backend.payment.web.dto.request.CreateSubscriptionRequest;
import com.kamylo.Scrtly_backend.payment.service.StripeService;
import com.kamylo.Scrtly_backend.payment.service.SubscriptionService;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.repository.UserRepository;
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

        String sessionId = stripe.createCheckoutSession(principal.getName(), req.getSuccessUrl(), req.getCancelUrl());
        return ResponseEntity.ok(Map.of("sessionId", sessionId));
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

    @GetMapping("/me")
    public ResponseEntity<SubscriptionDto> getMySubscription(Principal principal) {
        SubscriptionDto dto = subscriptionService.getMySubscription(principal.getName());
        if (dto == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dto);
    }
}