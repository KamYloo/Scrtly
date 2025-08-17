package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.auth.service.CookieService;
import com.kamylo.Scrtly_backend.auth.service.JwtService;
import com.kamylo.Scrtly_backend.auth.service.RefreshTokenService;
import com.kamylo.Scrtly_backend.auth.service.impl.OAuth2AuthenticationSuccessHandler;
import com.kamylo.Scrtly_backend.auth.web.dto.response.RefreshTokenResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.RedirectStrategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthenticationSuccessHandlerTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CookieService cookieService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private RedirectStrategy redirectStrategy;

    @InjectMocks
    private OAuth2AuthenticationSuccessHandler handler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private OidcUser oidcUser;

    @Captor
    private ArgumentCaptor<Cookie> cookieCaptor;

    private static final String REDIRECT_URL = "http://localhost:3000";

    @BeforeEach
    void setUp() {
        handler.setRedirectStrategy(redirectStrategy);
        handler.setRedirectUrl(REDIRECT_URL);
    }

    @Test
    void whenAuthenticationSuccess_thenGenerateTokenAndSetCookiesAndRedirect() throws Exception {
        // given
        String email = "user@example.com";
        String jwtToken = "jwt-token";
        String refreshToken = "refresh-token";

        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(email);
        when(jwtService.generateToken(email)).thenReturn(jwtToken);
        when(refreshTokenService.createRefreshToken(email))
                .thenReturn(new RefreshTokenResponse(refreshToken));

        Cookie jwtCookie = new Cookie("jwt", jwtToken);
        Cookie refreshCookie = new Cookie("refresh", refreshToken);
        when(cookieService.getNewCookie("jwt", jwtToken, 2 * 60 * 60)).thenReturn(jwtCookie);
        when(cookieService.getNewCookie("refresh", refreshToken, 7 * 24 * 60 * 60)).thenReturn(refreshCookie);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(response, times(2)).addCookie(cookieCaptor.capture());
        assertThat(cookieCaptor.getAllValues())
                .containsExactlyInAnyOrder(jwtCookie, refreshCookie);

        verify(redirectStrategy).sendRedirect(request, response,
                REDIRECT_URL + "/oauth2/redirect");
    }

    @Test
    void givenExceptionInTokenGeneration_thenPropagateRuntimeException() throws Exception {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn("email");
        when(jwtService.generateToken(anyString())).thenThrow(new RuntimeException("fail"));

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () ->
                handler.onAuthenticationSuccess(request, response, authentication)
        );

        verify(response, never()).addCookie(any());
        verify(redirectStrategy, never()).sendRedirect(any(), any(), anyString());
    }
}