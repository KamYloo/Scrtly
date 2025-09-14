package com.kamylo.Scrtly_backend.controllerTests;

import com.kamylo.Scrtly_backend.payment.service.StripeService;
import com.kamylo.Scrtly_backend.payment.web.controller.BillingPortalController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingPortalControllerTest {

    @Mock
    private StripeService portalService;

    @InjectMocks
    private BillingPortalController controller;

    @Mock
    private Principal principal;

    @Test
    void createPortal_returnsOk_andContainsUrl() {
        when(principal.getName()).thenReturn("user@example.com");
        when(portalService.createBillingPortalSession("user@example.com"))
                .thenReturn("https://portal.example/session/abc");

        var resp = controller.createPortal(principal);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("https://portal.example/session/abc", resp.getBody().get("url"));
        verify(portalService, times(1)).createBillingPortalSession("user@example.com");
    }
}