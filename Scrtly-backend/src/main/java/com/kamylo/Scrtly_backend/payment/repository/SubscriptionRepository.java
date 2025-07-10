package com.kamylo.Scrtly_backend.payment.repository;

import com.kamylo.Scrtly_backend.payment.domain.entity.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
    Optional<SubscriptionEntity> findByStripeSubscriptionId(String stripeId);
}
