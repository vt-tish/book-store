package com.vttish.bookstore.auth.service.impl;

import com.vttish.bookstore.auth.config.AuthProperties;
import com.vttish.bookstore.auth.dto.*;
import com.vttish.bookstore.auth.entity.RefreshToken;
import com.vttish.bookstore.auth.entity.ResetPasswordToken;
import com.vttish.bookstore.auth.entity.User;
import com.vttish.bookstore.auth.entity.VerifyToken;
import com.vttish.bookstore.auth.entity.enums.Role;
import com.vttish.bookstore.auth.exception.*;
import com.vttish.bookstore.auth.repository.RefreshTokenRepository;
import com.vttish.bookstore.auth.repository.ResetPasswordTokenRepository;
import com.vttish.bookstore.auth.repository.UserRepository;
import com.vttish.bookstore.auth.repository.VerifyTokenRepository;
import com.vttish.bookstore.auth.service.EmailService;
import com.vttish.bookstore.auth.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private VerifyTokenRepository verifyTokenRepository;
    @Mock private ResetPasswordTokenRepository resetPasswordTokenRepository;
    @Mock private JwtService jwtService;
    @Mock private EmailService emailService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthProperties authProps;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private AuthProperties.Token tokenProps;
    private AuthProperties.Jwt jwtProps;
    private AuthProperties.Jwt.RefreshToken refreshTokenProps;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "encoded_password", Role.CLIENT);
        testUser.verify();
    }

    private void mockTokenProps(long verifyMs, long resetMs, long cooldownMs) {
        tokenProps = mock(AuthProperties.Token.class);
        lenient().when(authProps.token()).thenReturn(tokenProps);
        lenient().when(tokenProps.verifyExpirationMs()).thenReturn(verifyMs);
        lenient().when(tokenProps.resetPasswordExpirationMs()).thenReturn(resetMs);
        lenient().when(tokenProps.cooldownPeriodMs()).thenReturn(cooldownMs);
    }

    private void mockJwtProps(long refreshMs, long graceMs) {
        jwtProps = mock(AuthProperties.Jwt.class);
        refreshTokenProps = mock(AuthProperties.Jwt.RefreshToken.class);
        lenient().when(authProps.jwt()).thenReturn(jwtProps);
        lenient().when(jwtProps.refreshToken()).thenReturn(refreshTokenProps);
        lenient().when(refreshTokenProps.expirationMs()).thenReturn(refreshMs);
        lenient().when(refreshTokenProps.gracePeriodMs()).thenReturn(graceMs);
    }

    @Test
    void register_ShouldSaveUserAndSendEmail() {
        mockTokenProps(3600000L, 0, 0);
        RegisterRequestDto dto = new RegisterRequestDto("test@example.com", "password123");
        VerifyToken token = new VerifyToken("opaque-token", testUser, Instant.now()
                .plusMillis(10000));

        when(passwordEncoder.encode(dto.password())).thenReturn("encoded_password");
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(testUser);
        when(jwtService.generateOpaqueToken()).thenReturn("opaque-token");
        when(verifyTokenRepository.save(any(VerifyToken.class))).thenReturn(token);

        authService.register(dto);

        verify(userRepository).saveAndFlush(any(User.class));
        verify(verifyTokenRepository).save(any(VerifyToken.class));
        verify(emailService).sendVerificationEmail("test@example.com", "opaque-token");
    }

    @Test
    void register_WhenEmailTaken_ShouldThrowException() {
        RegisterRequestDto dto = new RegisterRequestDto("test@example.com", "password123");
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded_password");
        when(userRepository.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        assertThrows(EmailTakenException.class, () -> authService.register(dto));
    }

    @Test
    void verify_ShouldVerifyUserAndReturnTokens() {
        mockJwtProps(86400000L, 0);
        VerifyToken token = new VerifyToken("token123", testUser, Instant.now().plusMillis(10000));
        when(verifyTokenRepository.findByToken("token123")).thenReturn(Optional.of(token));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        when(jwtService.generateOpaqueToken()).thenReturn("new-refresh");
        when(jwtService.generateAccessToken(testUser)).thenReturn("access-token");
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(i -> i.getArgument(0));

        TokensDto result = authService.verify(new VerifyRequestDto("token123"));

        assertEquals("new-refresh", result.refreshToken());
        assertEquals("access-token", result.accessToken());
        assertTrue(testUser.isVerified());
        verify(verifyTokenRepository).delete(token);
    }

    @Test
    void verify_WhenTokenExpired_ShouldThrowException() {
        VerifyToken token = new VerifyToken("token123", testUser, Instant.now()
                .minusMillis(10000));
        when(verifyTokenRepository.findByToken("token123")).thenReturn(Optional.of(token));

        assertThrows(ExpiredTokenException.class, () -> authService.verify(new VerifyRequestDto("token123")));
        verify(verifyTokenRepository).delete(token);
    }

    @Test
    void resendVerification_ShouldDeleteOldTokenAndSendNewEmail() {
        ReflectionTestUtils.setField(testUser, "isVerified", false);
        mockTokenProps(3600000L, 0, 60000L);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        
        VerifyToken oldToken = new VerifyToken("old", testUser, Instant.now().plusMillis(10000));

        ReflectionTestUtils.setField(oldToken, "createdAt", Instant.now().minusSeconds(120));
        
        when(verifyTokenRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(oldToken));
        when(jwtService.generateOpaqueToken()).thenReturn("new-token");
        when(verifyTokenRepository.saveAndFlush(any(VerifyToken.class)))
                .thenAnswer(i -> i.getArgument(0));

        authService.resendVerification(new ResendVerificationRequestDto("test@example.com"));

        verify(verifyTokenRepository).delete(oldToken);
        verify(emailService).sendVerificationEmail("test@example.com", "new-token");
    }

    @Test
    void resendVerification_WhenInCooldown_ShouldThrowException() {
        ReflectionTestUtils.setField(testUser, "isVerified", false);
        mockTokenProps(3600000L, 0, 600000L); // 10 minutes cooldown
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        
        VerifyToken oldToken = new VerifyToken("old", testUser, Instant.now().plusMillis(10000));

        ReflectionTestUtils.setField(oldToken, "createdAt", Instant.now().minusSeconds(10));
        
        when(verifyTokenRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(oldToken));

        assertThrows(EmailCooldownException.class, () -> 
            authService.resendVerification(new ResendVerificationRequestDto("test@example.com"))
        );
    }
    
    @Test
    void resendVerification_WhenUserNotFound_ShouldDoNothing() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        authService.resendVerification(new ResendVerificationRequestDto("test@example.com"));
        verify(verifyTokenRepository, never()).findByUserId(any());
    }

    @Test
    void login_ShouldReturnTokens() {
        mockJwtProps(86400000L, 0);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encoded_password"))
                .thenReturn(true);

        when(jwtService.generateOpaqueToken()).thenReturn("refresh-token");
        when(jwtService.generateAccessToken(testUser)).thenReturn("access-token");
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(i -> i.getArgument(0));

        TokensDto result = authService.login(new LoginRequestDto("test@example.com", "password123"));
        assertEquals("refresh-token", result.refreshToken());
        assertEquals("access-token", result.accessToken());
    }

    @Test
    void login_WhenUnverified_ShouldThrowException() {
        User unverifiedUser = new User("test@example.com", "encoded", Role.CLIENT);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(unverifiedUser));

        assertThrows(UnverifiedUserException.class, () -> 
            authService.login(new LoginRequestDto("test@example.com", "password123"))
        );
    }

    @Test
    void login_WhenBlocked_ShouldThrowException() {
        testUser.setBlocked(true);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        assertThrows(BlockedUserException.class, () -> 
            authService.login(new LoginRequestDto("test@example.com", "password123"))
        );
    }

    @Test
    void login_WhenWrongPassword_ShouldThrowException() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong", "encoded_password"))
                .thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> 
            authService.login(new LoginRequestDto("test@example.com", "wrong"))
        );
    }

    @Test
    void login_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> 
            authService.login(new LoginRequestDto("test@example.com", "wrong"))
        );
    }

    @Test
    void refresh_ShouldReturnNewTokens() {
        mockJwtProps(86400000L, 0);
        RefreshToken token = new RefreshToken(
                UUID.randomUUID(),
                "old-refresh",
                testUser,
                Instant.now().plusMillis(10000)
        );

        when(refreshTokenRepository.findByToken("old-refresh")).thenReturn(Optional.of(token));
        
        when(jwtService.generateOpaqueToken()).thenReturn("new-refresh");
        when(jwtService.generateAccessToken(testUser)).thenReturn("access-token");
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(i -> i.getArgument(0));

        TokensDto result = authService.refresh("old-refresh");
        assertEquals("new-refresh", result.refreshToken());
        assertEquals("access-token", result.accessToken());
        assertTrue(token.isConsumed());
    }

    @Test
    void refresh_WhenRevoked_ShouldThrowException() {
        RefreshToken token = new RefreshToken(
                UUID.randomUUID(),
                "old-refresh",
                testUser,
                Instant.now().plusMillis(10000)
        );

        token.revoke();
        when(refreshTokenRepository.findByToken("old-refresh")).thenReturn(Optional.of(token));

        assertThrows(RevokedTokenException.class, () -> authService.refresh("old-refresh"));
    }

    @Test
    void refresh_WhenExpired_ShouldRevokeAndThrowException() {
        RefreshToken token = new RefreshToken(
                UUID.randomUUID(),
                "old-refresh",
                testUser, Instant.now().minusMillis(10000)
        );

        when(refreshTokenRepository.findByToken("old-refresh")).thenReturn(Optional.of(token));

        assertThrows(ExpiredTokenException.class, () -> authService.refresh("old-refresh"));
        assertTrue(token.isRevoked());
        verify(refreshTokenRepository).save(token);
    }

    @Test
    void refresh_WhenConsumedWithinGracePeriod_ShouldReturnTokens() {
        mockJwtProps(86400000L, 60000L);
        RefreshToken token = new RefreshToken(
                UUID.randomUUID(),
                "old-refresh",
                testUser,
                Instant.now().plusMillis(10000)
        );

        token.consume("child-token");
        when(refreshTokenRepository.findByToken("old-refresh")).thenReturn(Optional.of(token));
        when(jwtService.generateAccessToken(testUser)).thenReturn("access-token");

        TokensDto result = authService.refresh("old-refresh");
        assertEquals("child-token", result.refreshToken());
        assertEquals("access-token", result.accessToken());
    }

    @Test
    void refresh_WhenConsumedAfterGracePeriod_ShouldRevokeFamilyAndThrowException() {
        mockJwtProps(86400000L, 0L);
        RefreshToken token = new RefreshToken(
                UUID.randomUUID(),
                "old-refresh",
                testUser,
                Instant.now().plusMillis(10000)
        );

        token.consume("child-token");
        
        ReflectionTestUtils.setField(token, "consumedAt", Instant.now().minusSeconds(60));
        
        when(refreshTokenRepository.findByToken("old-refresh")).thenReturn(Optional.of(token));

        assertThrows(TokenReuseException.class, () -> authService.refresh("old-refresh"));
        verify(refreshTokenRepository).revokeAllByFamilyId(token.getFamilyId());
        verify(emailService).sendSecurityAlert(testUser.getEmail());
    }

    @Test
    void logout_ShouldRevokeToken() {
        RefreshToken token = new RefreshToken(
                UUID.randomUUID(),
                "old-refresh",
                testUser,
                Instant.now().plusMillis(10000)
        );

        when(refreshTokenRepository.findByToken("old-refresh")).thenReturn(Optional.of(token));

        authService.logout("old-refresh");

        assertTrue(token.isRevoked());
        verify(refreshTokenRepository).save(token);
    }

    @Test
    void forgotPassword_ShouldCreateTokenAndSendEmail() {
        mockTokenProps(0, 3600000L, 60000L);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(resetPasswordTokenRepository.findByUserId(testUser.getId())).thenReturn(Optional.empty());
        when(jwtService.generateOpaqueToken()).thenReturn("reset-token");
        
        ResetPasswordToken token = new ResetPasswordToken(
                "reset-token",
                testUser,
                Instant.now().plusMillis(3600000L)
        );

        when(resetPasswordTokenRepository.save(any(ResetPasswordToken.class))).thenReturn(token);

        authService.forgotPassword(new ForgotPasswordRequestDto("test@example.com"));

        verify(emailService).sendResetPasswordEmail("test@example.com", "reset-token");
    }

    @Test
    void resetPassword_ShouldUpdatePasswordAndRevokeTokens() {
        ResetPasswordToken token = new ResetPasswordToken(
                "reset-token",
                testUser,
                Instant.now().plusMillis(3600000L)
        );

        when(resetPasswordTokenRepository.findByToken("reset-token")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("new-pass")).thenReturn("encoded-new");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        authService.resetPassword(new ResetPasswordRequestDto("reset-token", "new-pass"));

        assertEquals("encoded-new", testUser.getPassword());
        verify(refreshTokenRepository).revokeAllByUserId(testUser.getId());
        verify(resetPasswordTokenRepository).delete(token);
    }

    @Test
    void registerEmployee_ShouldSaveUserAndSendEmail() {
        mockTokenProps(3600000L, 0, 0);
        VerifyToken token = new VerifyToken(
                "opaque-token",
                testUser,
                Instant.now().plusMillis(10000)
        );

        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(testUser);
        when(jwtService.generateOpaqueToken()).thenReturn("opaque-token");
        when(verifyTokenRepository.save(any(VerifyToken.class))).thenReturn(token);

        User saved = authService.registerEmployee("emp@example.com", "password123");

        assertEquals(testUser, saved);
        verify(emailService).sendEmployeeVerificationEmail("emp@example.com", "opaque-token");
    }

    @Test
    void verifyEmployee_ShouldVerifyUser() {
        VerifyToken token = new VerifyToken(
                "token123",
                testUser,
                Instant.now().plusMillis(10000)
        );

        when(verifyTokenRepository.findByToken("token123")).thenReturn(Optional.of(token));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        authService.verifyEmployee("token123");

        assertTrue(testUser.isVerified());
        verify(verifyTokenRepository).delete(token);
    }

    @Test
    void resendVerification_WhenExistingTokenAbsent_ShouldSendEmail() {
        ReflectionTestUtils.setField(testUser, "isVerified", false);
        mockTokenProps(3600000L, 0, 60000L);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(verifyTokenRepository.findByUserId(testUser.getId())).thenReturn(Optional.empty());
        when(jwtService.generateOpaqueToken()).thenReturn("new-token");
        when(verifyTokenRepository.saveAndFlush(any(VerifyToken.class)))
                .thenAnswer(i -> i.getArgument(0));

        authService.resendVerification(new ResendVerificationRequestDto("test@example.com"));

        verify(verifyTokenRepository, never()).delete(any());
        verify(emailService).sendVerificationEmail("test@example.com", "new-token");
    }

    @Test
    void refresh_WhenTokenNotFound_ShouldThrowException() {
        when(refreshTokenRepository.findByToken("invalid")).thenReturn(Optional.empty());
        assertThrows(InvalidTokenException.class, () -> authService.refresh("invalid"));
    }

    @Test
    void refresh_WhenConsumedAtIsNull_ShouldThrowTokenReuseException() {
        RefreshToken token = new RefreshToken(
                UUID.randomUUID(),
                "old-refresh",
                testUser,
                Instant.now().plusMillis(10000)
        );

        token.consume("child-token");
        ReflectionTestUtils.setField(token, "consumedAt", null);
        when(refreshTokenRepository.findByToken("old-refresh")).thenReturn(Optional.of(token));

        assertThrows(TokenReuseException.class, () -> authService.refresh("old-refresh"));
        verify(refreshTokenRepository).revokeAllByFamilyId(token.getFamilyId());
        verify(emailService).sendSecurityAlert(testUser.getEmail());
    }

    @Test
    void forgotPassword_WhenUserNotFound_ShouldDoNothing() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        authService.forgotPassword(new ForgotPasswordRequestDto("test@example.com"));
        verify(resetPasswordTokenRepository, never()).findByUserId(any());
    }

    @Test
    void forgotPassword_WhenInCooldown_ShouldThrowException() {
        mockTokenProps(0, 3600000L, 600000L);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        ResetPasswordToken token = new ResetPasswordToken(
                "old-reset",
                testUser,
                Instant.now().plusMillis(3600000L)
        );

        ReflectionTestUtils.setField(token, "createdAt", Instant.now().minusSeconds(10));
        when(resetPasswordTokenRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(token));

        assertThrows(EmailCooldownException.class, () -> authService.forgotPassword(
                new ForgotPasswordRequestDto("test@example.com"))
        );
    }

    @Test
    void forgotPassword_WhenExistingTokenPastCooldown_ShouldDeleteOldToken() {
        mockTokenProps(0, 3600000L, 60000L);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        ResetPasswordToken oldToken = new ResetPasswordToken(
                "old-reset",
                testUser,
                Instant.now().plusMillis(3600000L)
        );

        ReflectionTestUtils.setField(oldToken, "createdAt", Instant.now().minusSeconds(120));
        when(resetPasswordTokenRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(oldToken));
        when(jwtService.generateOpaqueToken()).thenReturn("new-reset");
        when(resetPasswordTokenRepository.save(any(ResetPasswordToken.class)))
                .thenAnswer(i -> i.getArgument(0));

        authService.forgotPassword(new ForgotPasswordRequestDto("test@example.com"));

        verify(resetPasswordTokenRepository).delete(oldToken);
        verify(emailService).sendResetPasswordEmail("test@example.com", "new-reset");
    }

    @Test
    void resetPassword_WhenTokenNotFound_ShouldThrowException() {
        when(resetPasswordTokenRepository.findByToken("invalid")).thenReturn(Optional.empty());
        assertThrows(InvalidTokenException.class, () ->
                authService.resetPassword(new ResetPasswordRequestDto("invalid", "new-pass")));
    }

    @Test
    void resetPassword_WhenTokenExpired_ShouldThrowException() {
        ResetPasswordToken token = new ResetPasswordToken(
                "expired-reset",
                testUser,
                Instant.now().minusMillis(10000)
        );

        when(resetPasswordTokenRepository.findByToken("expired-reset")).thenReturn(Optional.of(token));
        assertThrows(ExpiredTokenException.class, () ->
                authService.resetPassword(
                        new ResetPasswordRequestDto("expired-reset", "new-pass")
                ));
        verify(resetPasswordTokenRepository).delete(token);
    }

    @Test
    void verify_WhenTokenNotFound_ShouldThrowException() {
        when(verifyTokenRepository.findByToken("invalid")).thenReturn(Optional.empty());
        assertThrows(InvalidTokenException.class, () -> authService.verify(new VerifyRequestDto("invalid")));
    }

    @Test
    void logout_WhenTokenNotFound_ShouldDoNothing() {
        when(refreshTokenRepository.findByToken("invalid")).thenReturn(Optional.empty());
        authService.logout("invalid");
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void resendVerification_WhenUserIsVerified_ShouldDoNothing() {
        User verifiedUser = mock(User.class);
        when(verifiedUser.isVerified()).thenReturn(true);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(verifiedUser));

        authService.resendVerification(new ResendVerificationRequestDto("test@example.com"));

        verify(verifyTokenRepository, never()).findByUserId(any());
        verify(emailService, never()).sendVerificationEmail(any(), any());
    }
}
