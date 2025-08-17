package com.kamylo.Scrtly_backend.payment.service;

import com.kamylo.Scrtly_backend.user.domain.UserEntity;

public interface StripeCustomerService {
    void ensureCustomer(UserEntity user);
}
