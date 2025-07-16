package com.kamylo.Scrtly_backend.payment.service.impl;

import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.payment.service.StripeCustomerService;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StripeCustomerServiceImpl implements StripeCustomerService {
    private final UserRepository userRepo;

    @Override
    @Transactional
    public void ensureCustomer(UserEntity user) {
        if (user.getStripeCustomerId() == null) {
            try {
                CustomerCreateParams custParams = CustomerCreateParams.builder()
                        .setEmail(user.getEmail())
                        .setName(user.getFullName())
                        .build();
                Customer customer = Customer.create(custParams);
                user.setStripeCustomerId(customer.getId());
                userRepo.save(user);
            } catch (StripeException e) {
                throw new CustomException(BusinessErrorCodes.STRIPE_API_ERROR, e);
            }
        }
    }
}

