package com.vttish.bookstore.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public record SecurityProperties(

        @Value("${book-store.security.jwt.access-token.secret-key}")
        String secretKey,

        @Value("${book-store.security.jwt.access-token.expiration-ms}")
        Long accessTokenExpirationMs,

        @Value("${book-store.security.jwt.refresh-token.expiration-ms}")
        Long refreshTokenExpirationMs,

        @Value("${book-store.security.jwt.refresh-token.grace-period-ms}")
        Long gracePeriodMs,

        @Value("${book-store.security.token.verify-expiration-ms}")
        Long verifyTokenExpirationMs,

        @Value("${book-store.security.token.reset-password-expiration-ms}")
        Long resetPasswordTokenExpirationMs,

        @Value("${book-store.security.token.cooldown-period-ms}")
        Long cooldownMs
) {}
