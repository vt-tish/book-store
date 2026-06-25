package com.vttish.bookstore.auth.service.impl;

import com.vttish.bookstore.auth.config.AuthProperties;
import com.vttish.bookstore.auth.entity.User;
import com.vttish.bookstore.auth.entity.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    @Mock
    private AuthProperties authProps;

    @InjectMocks
    private JwtServiceImpl jwtService;

    private User user;
    private UUID expectedId;
    
    @BeforeEach
    void setUp() {
        AuthProperties.Jwt jwtProps = mock(AuthProperties.Jwt.class);
        AuthProperties.Jwt.AccessToken accessProps = mock(AuthProperties.Jwt.AccessToken.class);
        
        lenient().when(authProps.jwt()).thenReturn(jwtProps);
        lenient().when(jwtProps.accessToken()).thenReturn(accessProps);

        lenient().when(accessProps.secretKey())
                .thenReturn("YmVsaWV2ZWR0ZW50Y292ZXJvbGRlc3RvY2N1cm5vZGRlZGNpdGl6ZW50b29rc29sYXI=");
        lenient().when(accessProps.expirationMs()).thenReturn(3600000L); // 1 hour

        user = mock(User.class);
        expectedId = UUID.randomUUID();
        lenient().when(user.getId()).thenReturn(expectedId);
        lenient().when(user.getRole()).thenReturn(Role.CLIENT);
    }

    @Test
    void generateAccessToken_ShouldReturnValidJwt() {
        String token = jwtService.generateAccessToken(user);
        assertNotNull(token);
        UUID extractedId = jwtService.extractUserId(token);
        assertEquals(expectedId, extractedId);
        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void generateOpaqueToken_ShouldReturnRandomUUID() {
        String token = jwtService.generateOpaqueToken();
        assertNotNull(token);
        assertDoesNotThrow(() -> UUID.fromString(token));
    }

    @Test
    void isTokenValid_ShouldReturnFalseForDifferentUser() {
        String token = jwtService.generateAccessToken(user);
        
        User wrongUser = mock(User.class);
        when(wrongUser.getId()).thenReturn(UUID.randomUUID());
        
        assertFalse(jwtService.isTokenValid(token, wrongUser));
    }

    @Test
    void isTokenValid_ShouldReturnFalseWhenTokenIsExpired() {
        AuthProperties.Jwt jwtProps = authProps.jwt();
        lenient().when(jwtProps.accessToken().expirationMs()).thenReturn(-1000L);
        
        String token = jwtService.generateAccessToken(user);
        
        assertFalse(jwtService.isTokenValid(token, user));
    }
}
