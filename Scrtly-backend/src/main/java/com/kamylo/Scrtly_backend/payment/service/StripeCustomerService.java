package com.kamylo.Scrtly_backend.payment.service;

import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.stripe.exception.StripeException;

public interface StripeCustomerService {
    void ensureCustomer(UserEntity user) throws StripeException;
}
