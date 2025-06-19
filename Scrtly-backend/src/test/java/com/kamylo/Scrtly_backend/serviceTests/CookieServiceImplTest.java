package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.auth.service.impl.CookieServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CookieServiceImplTest {

    private CookieServiceImpl cookieService;

    @BeforeEach
    void setUp() {
        cookieService = new CookieServiceImpl();
    }

    @Test
    void testGetNewCookie() {
        String cookieName = "myCookie";
        String cookieValue = "myValue";
        Cookie newCookie = cookieService.getNewCookie(cookieName, cookieValue, 24 * 60 * 60);

        assertEquals(cookieName, newCookie.getName());
        assertEquals(cookieValue, newCookie.getValue());
        assertTrue(newCookie.isHttpOnly());
        assertFalse(newCookie.getSecure());
        assertEquals("/", newCookie.getPath());
        assertEquals(24 * 60 * 60, newCookie.getMaxAge());
        assertEquals("Strict", newCookie.getAttribute("SameSite"));
    }

    @Test
    void testDeleteCookie() {
        String cookieName = "myCookie";
        Cookie deletedCookie = cookieService.deleteCookie(cookieName);

        assertEquals(cookieName, deletedCookie.getName());
        assertNull(deletedCookie.getValue());
        assertTrue(deletedCookie.isHttpOnly());
        assertFalse(deletedCookie.getSecure());
        assertEquals("/", deletedCookie.getPath());
        assertEquals(0, deletedCookie.getMaxAge());
        assertEquals("Strict", deletedCookie.getAttribute("SameSite"));
    }

    @Test
    void testGetCookieValueWhenCookieExists() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie cookie = new Cookie("session", "abc123");
        when(request.getCookies()).thenReturn(new Cookie[] { cookie });

        String cookieValue = cookieService.getCookieValue(request, "session");
        assertEquals("abc123", cookieValue);
    }

    @Test
    void testGetCookieValueWhenCookieNotExists() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie cookie = new Cookie("other", "value");
        when(request.getCookies()).thenReturn(new Cookie[] { cookie });

        assertThrows(CustomException.class, () ->
                cookieService.getCookieValue(request, "nonExistingCookie")
        );
    }

    @Test
    void testGetCookieValueWhenCookiesIsNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(null);

        assertThrows(CustomException.class, () ->
                cookieService.getCookieValue(request, "anyCookie")
        );
    }
}
