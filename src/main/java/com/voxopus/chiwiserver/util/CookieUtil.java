package com.voxopus.chiwiserver.util;

import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.Cookie;

public class CookieUtil {

    public static Cookie getCookie(Cookie[] cookies, String key) {
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(key)) {
                return cookie;
            }
        }
        return null;
    }

    public static ResponseCookie createResponseCookie(String key, String value, String path, int maxAge) {
        return ResponseCookie.from(key, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path(path)
                .build();
    }

}
