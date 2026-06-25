package com.vttish.bookstore.auth.service.impl;

import com.vttish.bookstore.auth.config.AuthProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CookieServiceImplTest {

    @Mock
    private AuthProperties authProps;

    @InjectMocks
    private CookieServiceImpl cookieService;

    @Test
    void createRefreshTokenCookie_ShouldReturnHeadersWithSetCookie() {
        AuthProperties.Cookie cookieProps = mock(AuthProperties.Cookie.class);
        AuthProperties.Jwt jwtProps = mock(AuthProperties.Jwt.class);
        AuthProperties.Jwt.RefreshToken refreshProps = mock(AuthProperties.Jwt.RefreshToken.class);
        
        when(authProps.cookie()).thenReturn(cookieProps);
        when(cookieProps.secure()).thenReturn(true);
        when(authProps.jwt()).thenReturn(jwtProps);
        when(jwtProps.refreshToken()).thenReturn(refreshProps);
        when(refreshProps.expirationMs()).thenReturn(86400000L);

        HttpHeaders headers = cookieService.createRefreshTokenCookie(
                "my-refresh-token", "/api/v1/auth"
        );

        assertTrue(headers.containsKey(HttpHeaders.SET_COOKIE));
        String setCookie = headers.getFirst(HttpHeaders.SET_COOKIE);
        assertNotNull(setCookie);
        assertThat(setCookie)
            .contains("refresh_token=my-refresh-token")
            .contains("HttpOnly")
            .contains("Secure")
            .contains("Path=/api/v1/auth")
            .contains("Max-Age=86400");
    }

    @Test
    void clearRefreshTokenCookie_ShouldReturnHeadersWithEmptyCookieAndZeroMaxAge() {
        AuthProperties.Cookie cookieProps = mock(AuthProperties.Cookie.class);
        when(authProps.cookie()).thenReturn(cookieProps);
        when(cookieProps.secure()).thenReturn(false);

        HttpHeaders headers = cookieService.clearRefreshTokenCookie("/api/v1/auth");

        assertTrue(headers.containsKey(HttpHeaders.SET_COOKIE));
        String setCookie = headers.getFirst(HttpHeaders.SET_COOKIE);
        assertNotNull(setCookie);
        assertThat(setCookie)
            .contains("refresh_token=")
            .contains("Max-Age=0")
            .contains("Path=/api/v1/auth")
            .contains("HttpOnly")
            .doesNotContain("Secure");
    }
}
