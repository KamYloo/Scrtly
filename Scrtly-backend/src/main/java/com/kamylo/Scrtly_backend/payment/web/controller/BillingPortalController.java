package com.kamylo.Scrtly_backend.payment.web.controller;

import com.kamylo.Scrtly_backend.payment.service.StripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/billing-portal")
@RequiredArgsConstructor
public class BillingPortalController {
    private final StripeService portalService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createPortal(Principal principal) {
        String url = portalService.createBillingPortalSession(principal.getName());
        return ResponseEntity.ok(Map.of("url", url));
    }
}
