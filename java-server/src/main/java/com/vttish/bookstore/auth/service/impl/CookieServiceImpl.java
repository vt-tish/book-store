package com.vttish.bookstore.auth.service.impl;

import com.vttish.bookstore.auth.service.CookieService;
import com.vttish.bookstore.common.constant.CookieConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CookieServiceImpl implements CookieService {

    @Value("${book-store.security.jwt.refresh-token.expiration-ms}")
    private Long refreshTokenExpirationMs;

    @Value("${book-store.security.cookie.secure}")
    private boolean isSecure;

    @Override
    public HttpHeaders createRefreshTokenCookie(String refreshToken, String path) {
        ResponseCookie cookie = ResponseCookie.from(CookieConstants.REFRESH_TOKEN, refreshToken)
                .httpOnly(true)
                .secure(isSecure)
                .sameSite("Strict")
                .maxAge(refreshTokenExpirationMs / 1000)
                .path(path)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return headers;
    }

    @Override
    public HttpHeaders clearRefreshTokenCookie(String path) {
        ResponseCookie cookie = ResponseCookie.from(CookieConstants.REFRESH_TOKEN, "")
                .httpOnly(true)
                .secure(isSecure)
                .sameSite("Strict")
                .maxAge(0)
                .path(path)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return headers;
    }
}
