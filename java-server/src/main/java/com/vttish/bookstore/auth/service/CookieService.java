package com.vttish.bookstore.auth.service;

import org.springframework.http.HttpHeaders;

public interface CookieService {
    HttpHeaders createRefreshTokenCookie(String refreshToken, String path);
    HttpHeaders clearRefreshTokenCookie(String path);
}
