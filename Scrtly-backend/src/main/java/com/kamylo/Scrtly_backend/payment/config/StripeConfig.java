package com.kamylo.Scrtly_backend.payment.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "stripe")
public class StripeConfig {
    private String apiKey;
    private String webhookSecret;
    private String priceIdMonthly;
    private String portalReturnUrl;

    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }
}
