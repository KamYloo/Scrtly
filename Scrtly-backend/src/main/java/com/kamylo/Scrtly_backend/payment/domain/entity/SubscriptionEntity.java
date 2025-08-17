package com.kamylo.Scrtly_backend.payment.domain.entity;

import com.kamylo.Scrtly_backend.payment.domain.enums.SubscriptionStatus;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscription")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class SubscriptionEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    private String stripeSubscriptionId;
    private LocalDateTime startDate;
    private LocalDateTime currentPeriodEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionStatus status;
}
