package com.vttish.bookstore.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "book-store.auth")
public record AuthProperties(
        Cors cors,
        Token token,
        Jwt jwt,
        Cookie cookie
) {
    public record Cors(boolean enabled, List<String> allowedOrigins) {}

    public record Token(
            long cooldownPeriodMs,
            long resetPasswordExpirationMs,
            long verifyExpirationMs
    ) {}

    public record Jwt(RefreshToken refreshToken, AccessToken accessToken) {
        public record RefreshToken(long expirationMs, long gracePeriodMs) {}
        public record AccessToken(long expirationMs, String secretKey) {}
    }

    public record Cookie(boolean secure) {}
}
