package com.kamylo.Scrtly_backend.repositoryTests;

import com.kamylo.Scrtly_backend.payment.domain.entity.SubscriptionEntity;
import com.kamylo.Scrtly_backend.payment.domain.enums.SubscriptionStatus;
import com.kamylo.Scrtly_backend.payment.repository.SubscriptionRepository;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SubscriptionRepositoryIntegrationTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        subscriptionRepository.deleteAll();
        em.flush();
    }

    private UserEntity persistUser(String base) {
        String uniq = String.valueOf(System.nanoTime());
        UserEntity u = UserEntity.builder()
                .fullName(base + " Full")
                .nickName((base + "_" + uniq).toLowerCase())
                .email((base + uniq).toLowerCase() + "@example.com")
                .password("secret")
                .enable(true)
                .build();
        em.persist(u);
        em.flush();
        return u;
    }

    private SubscriptionEntity persistSubscription(UserEntity user,
                                                   String stripeId,
                                                   LocalDateTime start,
                                                   LocalDateTime currentPeriodEnd,
                                                   SubscriptionStatus status) {
        SubscriptionEntity s = SubscriptionEntity.builder()
                .user(user)
                .stripeSubscriptionId(stripeId)
                .startDate(start)
                .currentPeriodEnd(currentPeriodEnd)
                .status(status)
                .build();
        em.persist(s);
        em.flush();
        return s;
    }

    @Test
    void findByStripeSubscriptionId_returnsSubscriptionWhenExists() {
        UserEntity u = persistUser("UserA");
        SubscriptionEntity s = persistSubscription(u, "sub_123", LocalDateTime.now().minusDays(10),
                LocalDateTime.now().plusDays(20), SubscriptionStatus.ACTIVE);

        Optional<SubscriptionEntity> found = subscriptionRepository.findByStripeSubscriptionId("sub_123");

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(s.getId());
        assertThat(found.get().getUser().getId()).isEqualTo(u.getId());
    }

    @Test
    void findAllByUserAndStatusIn_returnsOnlyRequestedStatuses() {
        UserEntity u = persistUser("UserB");
        persistSubscription(u, "s1", LocalDateTime.now().minusDays(5), LocalDateTime.now().plusDays(5), SubscriptionStatus.ACTIVE);
        persistSubscription(u, "s2", LocalDateTime.now().minusDays(20), LocalDateTime.now().minusDays(1), SubscriptionStatus.CANCELED);
        persistSubscription(u, "s3", LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(30), SubscriptionStatus.PENDING);

        List<SubscriptionEntity> results = subscriptionRepository.findAllByUserAndStatusIn(u, List.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.PENDING));

        assertThat(results).hasSize(2);
        assertThat(results).allSatisfy(sub -> assertThat(List.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.PENDING)).contains(sub.getStatus()));
    }

    @Test
    void existsByUserIdAndStatusAndCurrentPeriodEndAfter_behavesCorrectly() {
        UserEntity u = persistUser("UserC");
        // one active in future, one active expired
        persistSubscription(u, "future", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(2), SubscriptionStatus.ACTIVE);
        persistSubscription(u, "expired", LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(1), SubscriptionStatus.ACTIVE);

        boolean existsNow = subscriptionRepository.existsByUserIdAndStatusAndCurrentPeriodEndAfter(u.getId(), SubscriptionStatus.ACTIVE, LocalDateTime.now());
        assertThat(existsNow).isTrue();

        boolean existsAfterBig = subscriptionRepository.existsByUserIdAndStatusAndCurrentPeriodEndAfter(u.getId(), SubscriptionStatus.ACTIVE, LocalDateTime.now().plusDays(10));
        assertThat(existsAfterBig).isFalse();
    }

    @Test
    void findFirstByUserAndStatusOrderByCurrentPeriodEndDesc_returnsLatest() {
        UserEntity u = persistUser("UserD");
        persistSubscription(u, "old", LocalDateTime.now().minusDays(30), LocalDateTime.now().plusDays(1), SubscriptionStatus.ACTIVE);
        SubscriptionEntity latest = persistSubscription(u, "latest", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(40), SubscriptionStatus.ACTIVE);
        persistSubscription(u, "other-status", LocalDateTime.now().minusDays(5), LocalDateTime.now().plusDays(100), SubscriptionStatus.CANCELED);

        Optional<SubscriptionEntity> opt = subscriptionRepository.findFirstByUserAndStatusOrderByCurrentPeriodEndDesc(u, SubscriptionStatus.ACTIVE);

        assertThat(opt).isPresent();
        assertThat(opt.get().getId()).isEqualTo(latest.getId());
    }
}