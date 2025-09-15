package com.kamylo.Scrtly_backend.controllerTests;

import com.kamylo.Scrtly_backend.user.service.AdminService;
import com.kamylo.Scrtly_backend.user.web.controller.AdminController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController controller;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() throws Exception {
        Field f = AdminController.class.getDeclaredField("redirectUrl");
        f.setAccessible(true);
        f.set(controller, "http://frontend/verified");
    }

    @Test
    void active_account_callsService_andRedirects() throws Exception {
        Long userId = 7L;
        String token = "tok";

        var resp = controller.active_account(userId, token, response);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("User successfully verified as an artist", resp.getBody());
        verify(adminService, times(1)).verifyUserAsArtist(userId, token);
        verify(response, times(1)).sendRedirect("http://frontend/verified");
    }
}