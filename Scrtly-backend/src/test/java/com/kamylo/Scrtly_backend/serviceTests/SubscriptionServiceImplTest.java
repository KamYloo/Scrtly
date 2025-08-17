package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.payment.domain.entity.SubscriptionEntity;
import com.kamylo.Scrtly_backend.payment.domain.enums.SubscriptionStatus;
import com.kamylo.Scrtly_backend.payment.repository.SubscriptionRepository;
import com.kamylo.Scrtly_backend.payment.service.impl.SubscriptionServiceImpl;
import com.kamylo.Scrtly_backend.payment.web.dto.SubscriptionDto;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.service.UserService;
import com.stripe.model.Invoice;
import com.stripe.model.InvoiceLineItem;
import com.stripe.model.InvoiceLineItemCollection;
import com.stripe.model.checkout.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock private SubscriptionRepository repo;
    @Mock private UserService userService;

    @InjectMocks private SubscriptionServiceImpl service;

    private UserEntity createUser(Long id) {
        UserEntity u = new UserEntity();
        u.setId(id);
        return u;
    }

    private SubscriptionEntity createSubscriptionEntity(String stripeId, UserEntity user, LocalDateTime start, LocalDateTime currentEnd, SubscriptionStatus status) {
        SubscriptionEntity e = new SubscriptionEntity();
        e.setStripeSubscriptionId(stripeId);
        e.setUser(user);
        e.setStartDate(start);
        e.setCurrentPeriodEnd(currentEnd);
        e.setStatus(status);
        return e;
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void handleCheckoutSession_savesNewSubscription_whenNotExists() {
        Session session = mock(Session.class);
        when(session.getSubscription()).thenReturn("sub_123");
        long createdEpoch = 1_700_000_000L; // arbitrary epoch seconds
        when(session.getCreated()).thenReturn(createdEpoch);

        Long userId = 10L;
        UserEntity user = createUser(userId);
        when(userService.findUserById(userId)).thenReturn(user);

        when(repo.findByStripeSubscriptionId("sub_123")).thenReturn(Optional.empty());

        service.handleCheckoutSession(session, userId);

        ArgumentCaptor<SubscriptionEntity> captor = ArgumentCaptor.forClass(SubscriptionEntity.class);
        verify(repo).save(captor.capture());

        SubscriptionEntity saved = captor.getValue();
        assertEquals("sub_123", saved.getStripeSubscriptionId());
        assertEquals(user, saved.getUser());
        assertEquals(SubscriptionStatus.PENDING, saved.getStatus());

        LocalDateTime expectedStart = LocalDateTime.ofInstant(Instant.ofEpochSecond(createdEpoch), ZoneOffset.UTC);
        assertEquals(expectedStart, saved.getStartDate());
    }

    @Test
    void handleCheckoutSession_noopWhenSubscriptionAlreadyExists() {
        Session session = mock(Session.class);
        when(session.getSubscription()).thenReturn("sub_exist");
        when(repo.findByStripeSubscriptionId("sub_exist")).thenReturn(Optional.of(new SubscriptionEntity()));

        service.handleCheckoutSession(session, 1L);

        verify(repo, never()).save(any());
    }

    @Test
    void handleInvoicePaymentSucceeded_returnsWhenInvoiceHasNoParent() {
        Invoice invoice = mock(Invoice.class);
        when(invoice.getParent()).thenReturn(null);

        service.handleInvoicePaymentSucceeded(invoice);

        verify(repo, never()).findByStripeSubscriptionId(anyString());
    }

    @Test
    void handleInvoicePaymentSucceeded_updatesEndAndActivates_whenNewEndIsAfterCurrent() {
        Invoice invoice = mock(Invoice.class);

        Invoice.Parent parent = mock(Invoice.Parent.class);
        Invoice.Parent.SubscriptionDetails subDetails = mock(Invoice.Parent.SubscriptionDetails.class);

        when(invoice.getParent()).thenReturn(parent);
        when(parent.getSubscriptionDetails()).thenReturn(subDetails);
        when(subDetails.getSubscription()).thenReturn("sub_999");

        InvoiceLineItemCollection lines = mock(InvoiceLineItemCollection.class);
        InvoiceLineItem lineItem = mock(InvoiceLineItem.class);

        InvoiceLineItem.Period period = mock(InvoiceLineItem.Period.class);
        long periodEndEpoch = 1_700_100_000L;
        when(period.getEnd()).thenReturn(periodEndEpoch);

        when(lineItem.getPeriod()).thenReturn(period);
        when(lines.getData()).thenReturn(Collections.singletonList(lineItem));
        when(invoice.getLines()).thenReturn(lines);

        UserEntity u = createUser(77L);
        LocalDateTime oldEnd = LocalDateTime.ofInstant(Instant.ofEpochSecond(1_600_000_000L), ZoneOffset.UTC);
        SubscriptionEntity existing = createSubscriptionEntity("sub_999", u,
                LocalDateTime.now().minusDays(10), oldEnd, SubscriptionStatus.PENDING);

        when(repo.findByStripeSubscriptionId("sub_999")).thenReturn(Optional.of(existing));

        service.handleInvoicePaymentSucceeded(invoice);

        LocalDateTime expectedNewEnd = LocalDateTime.ofInstant(Instant.ofEpochSecond(periodEndEpoch), ZoneOffset.UTC);
        assertEquals(expectedNewEnd, existing.getCurrentPeriodEnd());
        assertEquals(SubscriptionStatus.ACTIVE, existing.getStatus());
        verify(repo).save(existing);
    }

    @Test
    void handleInvoicePaymentSucceeded_doesNotUpdate_whenExistingEndIsAfterNewEnd() {
        Invoice invoice = mock(Invoice.class);
        Invoice.Parent parent = mock(Invoice.Parent.class);
        Invoice.Parent.SubscriptionDetails subDetails = mock(Invoice.Parent.SubscriptionDetails.class);

        when(invoice.getParent()).thenReturn(parent);
        when(parent.getSubscriptionDetails()).thenReturn(subDetails);
        when(subDetails.getSubscription()).thenReturn("sub_old");

        InvoiceLineItemCollection lines = mock(InvoiceLineItemCollection.class);
        InvoiceLineItem lineItem = mock(InvoiceLineItem.class);
        InvoiceLineItem.Period period = mock(InvoiceLineItem.Period.class);

        long periodEndEpoch = 1_600_000_000L;
        when(period.getEnd()).thenReturn(periodEndEpoch);

        when(lineItem.getPeriod()).thenReturn(period);
        when(lines.getData()).thenReturn(Collections.singletonList(lineItem));
        when(invoice.getLines()).thenReturn(lines);

        UserEntity u = createUser(1L);

        LocalDateTime fixedStart = LocalDateTime.ofInstant(Instant.ofEpochSecond(1_500_000_000L), ZoneOffset.UTC);
        LocalDateTime existingEnd = LocalDateTime.ofInstant(Instant.ofEpochSecond(1_700_000_000L), ZoneOffset.UTC);
        SubscriptionEntity existing = createSubscriptionEntity("sub_old", u, fixedStart, existingEnd, SubscriptionStatus.PENDING);

        when(repo.findByStripeSubscriptionId("sub_old")).thenReturn(Optional.of(existing));

        service.handleInvoicePaymentSucceeded(invoice);

        assertEquals(existingEnd, existing.getCurrentPeriodEnd());
        assertNotEquals(SubscriptionStatus.ACTIVE, existing.getStatus());

        verify(repo, never()).save(any());
    }

    @Test
    void handleSubscriptionDeleted_setsCanceled_whenNotAlreadyCanceled() {
        com.stripe.model.Subscription stripeSub = mock(com.stripe.model.Subscription.class);
        when(stripeSub.getId()).thenReturn("stripe_sub_1");

        UserEntity u = createUser(5L);
        SubscriptionEntity existing = createSubscriptionEntity("stripe_sub_1", u, LocalDateTime.now(), null, SubscriptionStatus.ACTIVE);

        when(repo.findByStripeSubscriptionId("stripe_sub_1")).thenReturn(Optional.of(existing));

        service.handleSubscriptionDeleted(stripeSub);

        assertEquals(SubscriptionStatus.CANCELED, existing.getStatus());
        verify(repo).save(existing);
    }

    @Test
    void handleSubscriptionDeleted_noop_whenAlreadyCanceled() {
        com.stripe.model.Subscription stripeSub = mock(com.stripe.model.Subscription.class);
        when(stripeSub.getId()).thenReturn("stripe_sub_2");

        UserEntity u = createUser(6L);
        SubscriptionEntity existing = createSubscriptionEntity("stripe_sub_2", u, LocalDateTime.now(), null, SubscriptionStatus.CANCELED);

        when(repo.findByStripeSubscriptionId("stripe_sub_2")).thenReturn(Optional.of(existing));

        service.handleSubscriptionDeleted(stripeSub);

        verify(repo, never()).save(existing);
    }

    @Test
    void handleInvoicePaymentFailed_returnsWhenNoParent() {
        Invoice invoice = mock(Invoice.class);
        when(invoice.getParent()).thenReturn(null);

        service.handleInvoicePaymentFailed(invoice);

        verify(repo, never()).findByStripeSubscriptionId(anyString());
    }

    @Test
    void handleInvoicePaymentFailed_setsPastDue_whenSubscriptionFound() {
        Invoice invoice = mock(Invoice.class);

        Invoice.Parent parent = mock(Invoice.Parent.class);
        Invoice.Parent.SubscriptionDetails subDetails = mock(Invoice.Parent.SubscriptionDetails.class);

        when(invoice.getParent()).thenReturn(parent);
        when(parent.getSubscriptionDetails()).thenReturn(subDetails);
        when(subDetails.getSubscription()).thenReturn("sub_failed");

        UserEntity u = createUser(8L);
        SubscriptionEntity existing = createSubscriptionEntity("sub_failed", u, LocalDateTime.now(), null, SubscriptionStatus.ACTIVE);

        when(repo.findByStripeSubscriptionId("sub_failed")).thenReturn(Optional.of(existing));

        service.handleInvoicePaymentFailed(invoice);

        assertEquals(SubscriptionStatus.PAST_DUE, existing.getStatus());
        verify(repo).save(existing);
    }

    @Test
    void cancelLocalSubscription_setsCanceled_whenFound() {
        SubscriptionEntity existing = new SubscriptionEntity();
        existing.setStripeSubscriptionId("to_cancel");
        existing.setStatus(SubscriptionStatus.ACTIVE);

        when(repo.findByStripeSubscriptionId("to_cancel")).thenReturn(Optional.of(existing));

        service.cancelLocalSubscription("to_cancel");

        assertEquals(SubscriptionStatus.CANCELED, existing.getStatus());
        verify(repo).save(existing);
    }

    @Test
    void getMySubscription_returnsDto_whenActiveSubscriptionPresent() {
        String username = "u@example.com";
        UserEntity u = createUser(11L);
        when(userService.findUserByEmail(username)).thenReturn(u);

        LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(1_600_000_000L), ZoneOffset.UTC);
        LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(1_700_000_000L), ZoneOffset.UTC);

        SubscriptionEntity active = createSubscriptionEntity("s1", u, start, end, SubscriptionStatus.ACTIVE);

        when(repo.findFirstByUserAndStatusOrderByCurrentPeriodEndDesc(u, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(active));

        SubscriptionDto dto = service.getMySubscription(username);

        assertNotNull(dto);
        assertEquals(active.getStatus().name(), dto.getStatus());
        assertEquals(start, dto.getStartDate());
        assertEquals(end, dto.getCurrentPeriodEnd());
    }

    @Test
    void getMySubscription_returnsNull_whenNoActiveSubscription() {
        String username = "no@example.com";
        UserEntity u = createUser(22L);
        when(userService.findUserByEmail(username)).thenReturn(u);

        when(repo.findFirstByUserAndStatusOrderByCurrentPeriodEndDesc(u, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertNull(service.getMySubscription(username));
    }
}
