package com.kamylo.Scrtly_backend.controllerTests;

import com.kamylo.Scrtly_backend.payment.service.StripeService;
import com.kamylo.Scrtly_backend.payment.service.SubscriptionService;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookControllerTest {

    @Mock
    private StripeService stripe;

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private com.kamylo.Scrtly_backend.payment.web.controller.WebhookController controller;

    @Test
    void handle_checkoutSession_completed_invokesHandler() {
        Event event = mock(Event.class, RETURNS_DEEP_STUBS);
        Session session = mock(Session.class);
        when(session.getClientReferenceId()).thenReturn("123");
        when(event.getType()).thenReturn("checkout.session.completed");
        when(event.getDataObjectDeserializer().getObject()).thenReturn(Optional.of(session));
        when(stripe.constructEvent("payload", "sig")).thenReturn(event);

        ResponseEntity<Void> resp = controller.handle("payload", "sig");

        assertEquals(200, resp.getStatusCodeValue());
        verify(subscriptionService).handleCheckoutSession(session, 123L);
    }

    @Test
    void handle_invoice_payment_succeeded_invokesHandler() {
        Event event = mock(Event.class, RETURNS_DEEP_STUBS);
        Invoice invoice = mock(Invoice.class);
        when(event.getType()).thenReturn("invoice.payment_succeeded");
        when(event.getDataObjectDeserializer().getObject()).thenReturn(Optional.of(invoice));
        when(stripe.constructEvent("p", "s")).thenReturn(event);

        ResponseEntity<Void> resp = controller.handle("p", "s");

        assertEquals(200, resp.getStatusCodeValue());
        verify(subscriptionService).handleInvoicePaymentSucceeded(invoice);
    }

    @Test
    void handle_subscription_deleted_invokesHandler() {
        Event event = mock(Event.class, RETURNS_DEEP_STUBS);
        Subscription sub = mock(Subscription.class);
        when(event.getType()).thenReturn("customer.subscription.deleted");
        when(event.getDataObjectDeserializer().getObject()).thenReturn(Optional.of(sub));
        when(stripe.constructEvent("p2", "s2")).thenReturn(event);

        ResponseEntity<Void> resp = controller.handle("p2", "s2");

        assertEquals(200, resp.getStatusCodeValue());
        verify(subscriptionService).handleSubscriptionDeleted(sub);
    }

    @Test
    void handle_invoice_payment_failed_invokesHandler() {
        Event event = mock(Event.class, RETURNS_DEEP_STUBS);
        Invoice invoice = mock(Invoice.class);
        when(event.getType()).thenReturn("invoice.payment_failed");
        when(event.getDataObjectDeserializer().getObject()).thenReturn(Optional.of(invoice));
        when(stripe.constructEvent("p3", "s3")).thenReturn(event);

        ResponseEntity<Void> resp = controller.handle("p3", "s3");

        assertEquals(200, resp.getStatusCodeValue());
        verify(subscriptionService).handleInvoicePaymentFailed(invoice);
    }

    @Test
    void handle_unknown_type_noHandlersCalled() {
        Event event = mock(Event.class);
        when(event.getType()).thenReturn("some.other.event");
        when(stripe.constructEvent(anyString(), anyString())).thenReturn(event);

        ResponseEntity<Void> resp = controller.handle("x", "y");

        assertEquals(200, resp.getStatusCodeValue());
        verifyNoInteractions(subscriptionService);
    }
}
