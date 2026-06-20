package com.vttish.bookstore.auth.service.impl;

import com.vttish.bookstore.auth.config.SecurityProperties;
import com.vttish.bookstore.auth.dto.*;
import com.vttish.bookstore.auth.entity.RefreshToken;
import com.vttish.bookstore.auth.entity.ResetPasswordToken;
import com.vttish.bookstore.auth.entity.User;
import com.vttish.bookstore.auth.entity.VerifyToken;
import com.vttish.bookstore.auth.entity.enums.Role;
import com.vttish.bookstore.auth.repository.RefreshTokenRepository;
import com.vttish.bookstore.auth.repository.ResetPasswordTokenRepository;
import com.vttish.bookstore.auth.repository.UserRepository;
import com.vttish.bookstore.auth.repository.VerifyTokenRepository;
import com.vttish.bookstore.auth.service.AuthService;
import com.vttish.bookstore.auth.service.EmailService;
import com.vttish.bookstore.auth.service.JwtService;
import com.vttish.bookstore.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final VerifyTokenRepository verifyTokenRepository;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityProperties securityProperties;

    @Override
    @Transactional
    public void register(RegisterDto registerDto) {
        if (userRepository.findByEmail(registerDto.email()).isPresent()) {
            throw new BadRequestException("Email is already in use");
        }

        User user = userRepository.save(new User(
                registerDto.email(),
                passwordEncoder.encode(registerDto.password()),
                Role.CLIENT
        ));

        VerifyToken token = new VerifyToken(
                jwtService.generateOpaqueToken(),
                user,
                Instant.now().plusMillis(securityProperties.verifyTokenExpirationMs())
        );

        token = verifyTokenRepository.save(token);
        emailService.sendVerificationEmail(registerDto.email(), token.getToken());
    }

    @Override
    @Transactional
    public TokensDto verify(VerifyRequestDto verifyRequestDto) {
        VerifyToken verifyToken = verifyTokenRepository.findByToken(verifyRequestDto.token())
                .orElseThrow(() -> new BadCredentialsException("Invalid verify token"));

        if (verifyToken.isExpired()) {
            verifyTokenRepository.delete(verifyToken);
            throw new BadRequestException("Verification link is expired, request a new one");
        }

        User user = verifyToken.getUser();
        user.verify();

        user = userRepository.save(user);
        verifyTokenRepository.delete(verifyToken);

        return new TokensDto(generateNewRefreshToken(user), jwtService.generateAccessToken(user));
    }

    @Override
    @Transactional
    public void resendVerification(ResendVerificationRequestDto resendVerificationRequestDto) {
        Optional<User> userOptional = userRepository.findByEmail(
                resendVerificationRequestDto.email()
        );

        if (userOptional.isEmpty()) {
            return;
        }

        User user = userOptional.get();
        Optional<VerifyToken> existingToken = verifyTokenRepository.findByUserId(user.getId());

        if (existingToken.isPresent()) {
            if (existingToken.get().getCreatedAt()
                    .plusMillis(securityProperties.cooldownMs()).isAfter(Instant.now())
            ) {
                return;
            }

            verifyTokenRepository.delete(existingToken.get());
            verifyTokenRepository.flush();
        }

        VerifyToken token = new VerifyToken(
               jwtService.generateOpaqueToken(),
               user,
               Instant.now().plusMillis(securityProperties.verifyTokenExpirationMs())
        );

        try {
            token = verifyTokenRepository.saveAndFlush(token);
            emailService.sendVerificationEmail(user.getEmail(), token.getToken());
        } catch (DataIntegrityViolationException ignored) {}
    }

    @Override
    @Transactional
    public TokensDto login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.email()).orElseThrow(() ->
                new BadCredentialsException("Invalid email or password")
        );

        if (!user.isVerified()) {
            throw new BadRequestException("User is not verified");
        }

        if (user.isBlocked()) {
            throw new BadRequestException("User has been blocked");
        }

        if (!passwordEncoder.matches(loginDto.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        return new TokensDto(generateNewRefreshToken(user), jwtService.generateAccessToken(user));
    }

    @Override
    @Transactional
    public TokensDto refresh(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(() ->
                new BadCredentialsException("Invalid refresh token")
        );

        if (refreshToken.isRevoked()) {
            throw new BadCredentialsException("Session terminated, log in again");
        }

        User user = refreshToken.getUser();
        if (refreshToken.isConsumed()) {
            if (refreshToken.getConsumedAt() != null &&
                    refreshToken.getConsumedAt().plusMillis(
                            securityProperties.gracePeriodMs()
                    ).isAfter(Instant.now())
            ) {
                return new TokensDto(
                        refreshToken.getChildToken(),
                        jwtService.generateAccessToken(user)
                );
            }

            revokeRefreshTokenFamily(refreshToken.getFamilyId());
            emailService.sendSecurityAlert(user.getEmail());
            throw new BadCredentialsException("Token reuse detected, session terminated");
        }

        if (refreshToken.isExpired()) {
            refreshToken.revoke();
            refreshTokenRepository.save(refreshToken);
            throw new BadCredentialsException("Refresh token expired, log in again");
        }

        String newToken = jwtService.generateOpaqueToken();
        RefreshToken newRefreshToken = new RefreshToken(
                refreshToken.getFamilyId(),
                newToken,
                user,
                refreshToken.getExpiresAt()
        );

        refreshToken.consume(newToken);
        refreshTokenRepository.save(refreshToken);
        newRefreshToken = refreshTokenRepository.save(newRefreshToken);

        return new TokensDto(newRefreshToken.getToken(), jwtService.generateAccessToken(user));
    }

    @Override
    @Transactional
    public void logout(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshToken -> {
            refreshToken.revoke();
            refreshTokenRepository.save(refreshToken);
        });
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequestDto forgotPasswordRequestDto) {
        Optional<User> userOptional = userRepository.findByEmail(forgotPasswordRequestDto.email());

        if (userOptional.isEmpty()) {
            return;
        }

        User user = userOptional.get();
        Optional<ResetPasswordToken> existingToken = resetPasswordTokenRepository.findByUserId(user.getId());

        if (existingToken.isPresent()) {
            if (existingToken.get().getCreatedAt()
                    .plusMillis(securityProperties.cooldownMs()).isAfter(Instant.now())
            ) {
                return;
            }

            resetPasswordTokenRepository.delete(existingToken.get());
            resetPasswordTokenRepository.flush();
        }

        ResetPasswordToken token = new ResetPasswordToken(
                jwtService.generateOpaqueToken(),
                user,
                Instant.now().plusMillis(securityProperties.resetPasswordTokenExpirationMs())
        );

        try {
            token = resetPasswordTokenRepository.saveAndFlush(token);
            emailService.sendPasswordResetEmail(user.getEmail(), token.getToken());
        } catch (DataIntegrityViolationException ignored) {}
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequestDto resetPasswordRequestDto) {
        ResetPasswordToken token = resetPasswordTokenRepository.findByToken(
                resetPasswordRequestDto.token()
        ).orElseThrow(() -> new BadCredentialsException("Invalid reset password token"));

        if (token.isExpired()) {
            resetPasswordTokenRepository.delete(token);
            throw new BadRequestException("Reset password link is expired, request a new one");
        }

        User user = token.getUser();
        user.setPassword(
                passwordEncoder.encode(resetPasswordRequestDto.newPassword())
        );

        user = userRepository.save(user);
        revokeRefreshTokensByUserId(user.getId());
        resetPasswordTokenRepository.delete(token);
    }

    public String generateNewRefreshToken(User user) {
        RefreshToken refreshToken = refreshTokenRepository.save(new RefreshToken(
                jwtService.generateOpaqueToken(),
                user,
                Instant.now().plusMillis(securityProperties.refreshTokenExpirationMs())
        ));

        return refreshToken.getToken();
    }

    private void revokeRefreshTokenFamily(UUID familyId) {
        revokeRefreshTokens(refreshTokenRepository.findAllByFamilyId(familyId));
    }

    private void revokeRefreshTokensByUserId(UUID userId) {
        revokeRefreshTokens(refreshTokenRepository.findAllByUserId(userId));
    }

    private void revokeRefreshTokens(List<RefreshToken> refreshTokens) {
        if (refreshTokens.isEmpty()) {
            return;
        }

        refreshTokens.forEach(RefreshToken::revoke);
        refreshTokenRepository.saveAll(refreshTokens);
    }
}
