package com.kamylo.Scrtly_backend.payment.web.controller;

import com.kamylo.Scrtly_backend.payment.service.StripeService;
import com.kamylo.Scrtly_backend.payment.service.SubscriptionService;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.repository.UserRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {
    private final StripeService stripe;
    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<Void> handle(@RequestBody String payload,
                                       @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event = stripe.constructEvent(payload, sigHeader);
        switch (event.getType()) {
            case "checkout.session.completed":
                Session session = (Session) event.getDataObjectDeserializer()
                        .getObject().orElseThrow();
                Long userId = Long.valueOf(session.getClientReferenceId());
                subscriptionService.handleCheckoutSession(session, userId);
                break;
            case "invoice.payment_succeeded":
                Invoice invoice = (Invoice) event.getDataObjectDeserializer()
                        .getObject().orElseThrow();
                subscriptionService.handleInvoicePaymentSucceeded(invoice);
                break;
            case "customer.subscription.deleted":
                Subscription sub = (Subscription) event.getDataObjectDeserializer()
                        .getObject().orElseThrow();
                subscriptionService.handleSubscriptionDeleted(sub);
                break;
            case "invoice.payment_failed":
                Invoice inv = (Invoice) event.getDataObjectDeserializer().getObject().orElseThrow();
                subscriptionService.handleInvoicePaymentFailed(inv);
                break;
            default:
        }
        return ResponseEntity.ok().build();
    }
}