package com.kamylo.Scrtly_backend.payment.repository;

import com.kamylo.Scrtly_backend.payment.domain.entity.SubscriptionEntity;
import com.kamylo.Scrtly_backend.payment.domain.enums.SubscriptionStatus;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
    Optional<SubscriptionEntity> findByStripeSubscriptionId(String stripeId);
    List<SubscriptionEntity> findAllByUserAndStatusIn(UserEntity user, List<SubscriptionStatus> statuses);
    boolean existsByUserIdAndStatusAndCurrentPeriodEndAfter(Long userId, SubscriptionStatus status, LocalDateTime now);
}
