package com.kamylo.Scrtly_backend.controllerTests;

import com.kamylo.Scrtly_backend.payment.domain.entity.SubscriptionEntity;
import com.kamylo.Scrtly_backend.payment.service.StripeService;
import com.kamylo.Scrtly_backend.payment.service.SubscriptionService;
import com.kamylo.Scrtly_backend.payment.web.controller.SubscriptionController;
import com.kamylo.Scrtly_backend.payment.web.dto.SubscriptionDto;
import com.kamylo.Scrtly_backend.payment.web.dto.request.CreateSubscriptionRequest;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    @Mock
    private StripeService stripe;

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private SubscriptionController controller;

    @Mock
    private Principal principal;

    @Test
    void create_returnsSessionId() {
        when(principal.getName()).thenReturn("user@example.com");
        CreateSubscriptionRequest req = new CreateSubscriptionRequest();
        req.setSuccessUrl("https://ok");
        req.setCancelUrl("https://cancel");

        when(stripe.createCheckoutSession("user@example.com", "https://ok", "https://cancel"))
                .thenReturn("sess_123");

        ResponseEntity<java.util.Map<String, String>> resp = controller.create(principal, req);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("sess_123", resp.getBody().get("sessionId"));
        verify(stripe).createCheckoutSession("user@example.com", "https://ok", "https://cancel");
    }

    @Test
    void cancelSubscription_whenBelongs_callsServices_andReturnsOk() {
        when(principal.getName()).thenReturn("owner@ex.com");
        String subscriptionId = "sub_1";

        UserEntity user = mock(UserEntity.class);
        SubscriptionEntity sub = mock(SubscriptionEntity.class);
        when(sub.getStripeSubscriptionId()).thenReturn(subscriptionId);
        doReturn(List.of(sub)).when(user).getSubscriptions();
        when(userRepo.findByEmail("owner@ex.com")).thenReturn(Optional.of(user));

        ResponseEntity<Void> resp = controller.cancelSubscription(principal, subscriptionId);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(stripe).cancelSubscription(subscriptionId);
        verify(subscriptionService).cancelLocalSubscription(subscriptionId);
    }

    @Test
    void cancelSubscription_whenNotBelongs_returnsForbidden() {
        when(principal.getName()).thenReturn("other@ex.com");
        String subscriptionId = "sub_2";

        UserEntity user = mock(UserEntity.class);
        SubscriptionEntity sub = mock(SubscriptionEntity.class);
        when(sub.getStripeSubscriptionId()).thenReturn("different");
        doReturn(List.of(sub)).when(user).getSubscriptions();
        when(userRepo.findByEmail("other@ex.com")).thenReturn(Optional.of(user));

        ResponseEntity<Void> resp = controller.cancelSubscription(principal, subscriptionId);

        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
        verify(stripe, never()).cancelSubscription(anyString());
        verify(subscriptionService, never()).cancelLocalSubscription(anyString());
    }

    @Test
    void getMySubscription_whenNone_returnsNoContent() {
        when(principal.getName()).thenReturn("u@ex.com");
        when(subscriptionService.getMySubscription("u@ex.com")).thenReturn(null);

        ResponseEntity<SubscriptionDto> resp = controller.getMySubscription(principal);

        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    @Test
    void getMySubscription_whenPresent_returnsOk() {
        when(principal.getName()).thenReturn("u2@ex.com");
        SubscriptionDto dto = new SubscriptionDto();
        dto.setId(1L);
        when(subscriptionService.getMySubscription("u2@ex.com")).thenReturn(dto);

        ResponseEntity<SubscriptionDto> resp = controller.getMySubscription(principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(dto, resp.getBody());
    }
}