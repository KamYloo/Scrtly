package com.kamylo.Scrtly_backend.payment.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateSubscriptionRequest {
    @NotBlank
    private String successUrl;

    @NotBlank
    private String cancelUrl;
}
