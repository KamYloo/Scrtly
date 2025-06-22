package com.kamylo.Scrtly_backend.auth.service.impl;

import com.kamylo.Scrtly_backend.auth.service.CookieService;
import com.kamylo.Scrtly_backend.auth.service.JwtService;
import com.kamylo.Scrtly_backend.auth.service.RefreshTokenService;
import com.kamylo.Scrtly_backend.auth.web.dto.response.RefreshTokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final CookieService cookieService;
    private final RefreshTokenService refreshTokenService;

    @Value("${mailing.frontend.redirect-url}")
    private String redirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OidcUser user = (OidcUser) authentication.getPrincipal();
        String token = jwtService.generateToken(user.getEmail());
        RefreshTokenResponse refreshResp = refreshTokenService.createRefreshToken(user.getEmail());
        response.addCookie(cookieService.getNewCookie("jwt_zuvoria_v1", token, 2 * 60 * 60));
        response.addCookie(cookieService.getNewCookie("refresh_zuvoria_v1", refreshResp.getRefreshToken(), 7 * 24 * 60 * 60));
        getRedirectStrategy().sendRedirect(request, response, redirectUrl+"/oauth2/redirect");
    }

}
