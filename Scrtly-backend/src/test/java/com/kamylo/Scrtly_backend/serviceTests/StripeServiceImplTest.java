package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.payment.config.StripeConfig;
import com.kamylo.Scrtly_backend.payment.domain.entity.SubscriptionEntity;
import com.kamylo.Scrtly_backend.payment.domain.enums.SubscriptionStatus;
import com.kamylo.Scrtly_backend.payment.repository.SubscriptionRepository;
import com.kamylo.Scrtly_backend.payment.service.StripeCustomerService;
import com.kamylo.Scrtly_backend.payment.service.impl.StripeServiceImpl;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.service.UserService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.net.RequestOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripeServiceImplTest {

    @Mock private StripeConfig cfg;
    @Mock private UserService userService;
    @Mock private StripeCustomerService customerService;
    @Mock private SubscriptionRepository subscriptionRepository;

    @InjectMocks private StripeServiceImpl service;

    private UserEntity createUser(Long id, String email, String fullName, String stripeCustomerId) {
        UserEntity u = new UserEntity();
        u.setId(id);
        u.setEmail(email);
        u.setFullName(fullName);
        u.setStripeCustomerId(stripeCustomerId);
        return u;
    }

    static class TestStripeException extends StripeException {
        public TestStripeException() {
            super("stripe test error", null, null, null);
        }
    }

    @Test
    void createCheckoutSession_returnsSessionId_whenNoActiveSubscription() {
        String username = "u@example.com";
        UserEntity u = createUser(42L, username, "User Name", "cus_1");
        when(userService.findUserByEmail(username)).thenReturn(u);
        when(subscriptionRepository.findAllByUserAndStatusIn(eq(u), anyList())).thenReturn(Collections.emptyList());
        when(cfg.getPriceIdMonthly()).thenReturn("price_monthly");

        Session sessionMock = mock(Session.class);
        when(sessionMock.getId()).thenReturn("sess_123");

        try (MockedStatic<Session> mocked = mockStatic(Session.class)) {
            mocked.when(() -> Session.create(any(SessionCreateParams.class), any(RequestOptions.class)))
                    .thenReturn(sessionMock);

            String result = service.createCheckoutSession(username, "https://ok", "https://cancel");

            assertEquals("sess_123", result);
            verify(customerService).ensureCustomer(u);
            mocked.verify(() -> Session.create(any(SessionCreateParams.class), any(RequestOptions.class)), times(1));
        }
    }

    @Test
    void createCheckoutSession_throwsWhenAlreadySubscribed() {
        String username = "a@ex.com";
        UserEntity u = createUser(1L, username, "Some", null);
        when(userService.findUserByEmail(username)).thenReturn(u);
        SubscriptionEntity sub = new SubscriptionEntity();
        sub.setStatus(SubscriptionStatus.ACTIVE);
        when(subscriptionRepository.findAllByUserAndStatusIn(eq(u), anyList())).thenReturn(List.of(sub));

        CustomException ex = assertThrows(CustomException.class,
                () -> service.createCheckoutSession(username, "s", "c"));
        assertEquals(BusinessErrorCodes.USER_ALREADY_SUBSCRIBED, ex.getErrorCode());
        verifyNoInteractions(customerService);
    }

    @Test
    void createCheckoutSession_wrapsStripeException() {
        String username = "u2@example.com";
        UserEntity u = createUser(2L, username, "User2", "cus_x");
        when(userService.findUserByEmail(username)).thenReturn(u);
        when(subscriptionRepository.findAllByUserAndStatusIn(eq(u), anyList())).thenReturn(Collections.emptyList());
        when(cfg.getPriceIdMonthly()).thenReturn("price_monthly");

        try (MockedStatic<Session> mocked = mockStatic(Session.class)) {
            mocked.when(() -> Session.create(any(SessionCreateParams.class), any(RequestOptions.class)))
                    .thenThrow(new TestStripeException());

            CustomException ex = assertThrows(CustomException.class,
                    () -> service.createCheckoutSession(username, "s", "c"));
            assertEquals(BusinessErrorCodes.STRIPE_API_ERROR, ex.getErrorCode());
        }
    }

    @Test
    void constructEvent_returnsEvent_whenValid() {
        String payload = "{}";
        String sig = "sig";
        when(cfg.getWebhookSecret()).thenReturn("whsec_test");

        Event eventMock = mock(Event.class);
        try (MockedStatic<com.stripe.net.Webhook> mocked = mockStatic(com.stripe.net.Webhook.class)) {
            mocked.when(() -> com.stripe.net.Webhook.constructEvent(payload, sig, "whsec_test"))
                    .thenReturn(eventMock);

            Event e = service.constructEvent(payload, sig);
            assertSame(eventMock, e);
        }
    }

    @Test
    void constructEvent_throwsWhenSignatureInvalid() {
        String payload = "{}";
        String sig = "bad";
        when(cfg.getWebhookSecret()).thenReturn("whsec_test");

        try (MockedStatic<com.stripe.net.Webhook> mocked = mockStatic(com.stripe.net.Webhook.class)) {
            mocked.when(() -> com.stripe.net.Webhook.constructEvent(payload, sig, "whsec_test"))
                    .thenThrow(new SignatureVerificationException("bad sig", null));

            CustomException ex = assertThrows(CustomException.class, () -> service.constructEvent(payload, sig));
            assertEquals(BusinessErrorCodes.STRIPE_WEBHOOK_SIGNATURE_INVALID, ex.getErrorCode());
        }
    }

    @Test
    void cancelSubscription_returnsSubscription_whenSuccessful() throws Exception {
        String subscriptionId = "sub_abc";
        Subscription subMock = mock(Subscription.class);
        when(subMock.cancel()).thenReturn(subMock);

        try (MockedStatic<Subscription> mocked = mockStatic(Subscription.class)) {
            mocked.when(() -> Subscription.retrieve(subscriptionId)).thenReturn(subMock);

            Subscription result = service.cancelSubscription(subscriptionId);
            assertSame(subMock, result);
            mocked.verify(() -> Subscription.retrieve(subscriptionId), times(1));
        }
    }

    @Test
    void cancelSubscription_wrapsStripeException() {
        String subscriptionId = "sub_err";

        try (MockedStatic<Subscription> mocked = mockStatic(Subscription.class)) {
            mocked.when(() -> Subscription.retrieve(subscriptionId)).thenThrow(new TestStripeException());

            CustomException ex = assertThrows(CustomException.class, () -> service.cancelSubscription(subscriptionId));
            assertEquals(BusinessErrorCodes.STRIPE_API_ERROR, ex.getErrorCode());
        }
    }

    @Test
    void createBillingPortalSession_returnsUrl_whenOk() {
        String username = "portal@ex.com";
        UserEntity u = createUser(99L, username, "Portal User", "cus_portal");
        when(userService.findUserByEmail(username)).thenReturn(u);
        when(cfg.getPortalReturnUrl()).thenReturn("https://return");

        com.stripe.model.billingportal.Session portalSessionMock = mock(com.stripe.model.billingportal.Session.class);
        when(portalSessionMock.getUrl()).thenReturn("https://portal");

        try (MockedStatic<com.stripe.model.billingportal.Session> mocked =
                     mockStatic(com.stripe.model.billingportal.Session.class)) {
            mocked.when(() -> com.stripe.model.billingportal.Session.create(any(com.stripe.param.billingportal.SessionCreateParams.class)))
                    .thenReturn(portalSessionMock);

            String url = service.createBillingPortalSession(username);
            assertEquals("https://portal", url);
            mocked.verify(() -> com.stripe.model.billingportal.Session.create(any(com.stripe.param.billingportal.SessionCreateParams.class)), times(1));
        }
    }

    @Test
    void createBillingPortalSession_wrapsStripeException() {
        String username = "portal2@ex.com";
        UserEntity u = createUser(100L, username, "Portal2", "cus_x");
        when(userService.findUserByEmail(username)).thenReturn(u);
        when(cfg.getPortalReturnUrl()).thenReturn("https://return");

        try (MockedStatic<com.stripe.model.billingportal.Session> mocked =
                     mockStatic(com.stripe.model.billingportal.Session.class)) {
            mocked.when(() -> com.stripe.model.billingportal.Session.create(any(com.stripe.param.billingportal.SessionCreateParams.class)))
                    .thenThrow(new TestStripeException());

            CustomException ex = assertThrows(CustomException.class, () -> service.createBillingPortalSession(username));
            assertEquals(BusinessErrorCodes.STRIPE_API_ERROR, ex.getErrorCode());
        }
    }
}
