package com.kamylo.Scrtly_backend.service.impl;

import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.service.CookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class CookieServiceImpl implements CookieService {
    @Override
    public Cookie getNewCookie(String arg, String value) {
        Cookie cookie = new Cookie(arg, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setAttribute("SameSite", "Strict");
        return cookie;
    }

    @Override
    public Cookie deleteCookie(String arg) {
        Cookie cookie = new Cookie(arg, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Strict");
        return cookie;
    }

    @Override
    public String getCookieValue(HttpServletRequest request, String cookieName) {
        return Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new CustomException(BusinessErrorCodes.BAD_COOKIE));
    }

}
