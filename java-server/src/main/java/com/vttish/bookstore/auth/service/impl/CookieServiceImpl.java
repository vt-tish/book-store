package com.vttish.bookstore.auth.service.impl;

import com.vttish.bookstore.auth.config.AuthProperties;
import com.vttish.bookstore.auth.service.CookieService;
import com.vttish.bookstore.common.constant.CookieConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookieServiceImpl implements CookieService {
    private final AuthProperties authProps;

    @Override
    public HttpHeaders createRefreshTokenCookie(String refreshToken, String path) {
        ResponseCookie cookie = ResponseCookie.from(CookieConstants.REFRESH_TOKEN, refreshToken)
                .httpOnly(true)
                .secure(authProps.cookie().secure())
                .sameSite("Strict")
                .maxAge(authProps.jwt().refreshToken().expirationMs() / 1000)
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
                .secure(authProps.cookie().secure())
                .sameSite("Strict")
                .maxAge(0)
                .path(path)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return headers;
    }
}
