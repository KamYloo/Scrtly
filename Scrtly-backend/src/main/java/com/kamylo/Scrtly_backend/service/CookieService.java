package com.kamylo.Scrtly_backend.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public interface CookieService {
    Cookie getNewCookie(String arg, String value, int maxAgeSeconds);
    Cookie deleteCookie(String arg);
    String getCookieValue(HttpServletRequest request, String cookieName);
}