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
import com.vttish.bookstore.auth.service.AuthService;
import com.vttish.bookstore.auth.service.EmailService;
import com.vttish.bookstore.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final VerifyTokenRepository verifyTokenRepository;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthProperties authProps;

    @Override
    @Transactional
    public void register(RegisterRequestDto registerRequestDto) {
        VerifyToken token = registerWithRole(registerRequestDto.email(), registerRequestDto.password(), Role.CLIENT);
        emailService.sendVerificationEmail(registerRequestDto.email(), token.getToken());
    }

    @Override
    @Transactional
    public TokensDto verify(VerifyRequestDto verifyRequestDto) {
        User user = verifyUser(verifyRequestDto.token());
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
            long secondsSinceLastEmail = Duration.between(
                    existingToken.get().getCreatedAt(), Instant.now()).getSeconds();
            long cooldownSeconds = authProps.token().cooldownPeriodMs() / 1000;

            if (secondsSinceLastEmail < cooldownSeconds) {
                throw new EmailCooldownException(cooldownSeconds - secondsSinceLastEmail);
            }

            verifyTokenRepository.delete(existingToken.get());
        }

        VerifyToken token = new VerifyToken(
               jwtService.generateOpaqueToken(),
               user,
               Instant.now().plusMillis(authProps.token().verifyExpirationMs())
        );

        try {
            token = verifyTokenRepository.saveAndFlush(token);
            emailService.sendVerificationEmail(user.getEmail(), token.getToken());
        } catch (DataIntegrityViolationException ignored) {
            log.warn("Concurrent verify token creation, token exists");
        }
    }

    @Override
    @Transactional
    public TokensDto login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.email()).orElseThrow(
                InvalidCredentialsException::new
        );

        if (!user.isVerified()) {
            throw new UnverifiedUserException();
        }

        if (user.isBlocked()) {
            throw new BlockedUserException();
        }

        if (!passwordEncoder.matches(loginRequestDto.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return new TokensDto(generateNewRefreshToken(user), jwtService.generateAccessToken(user));
    }

    @Override
    @Transactional
    public TokensDto refresh(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(
                InvalidTokenException::new
        );

        if (refreshToken.isRevoked()) {
            throw new RevokedTokenException();
        }

        User user = refreshToken.getUser();
        if (refreshToken.isConsumed()) {
            if (refreshToken.getConsumedAt() != null &&
                    refreshToken.getConsumedAt().plusMillis(
                            authProps.jwt().refreshToken().gracePeriodMs()
                    ).isAfter(Instant.now())
            ) {
                return new TokensDto(
                        refreshToken.getChildToken(),
                        jwtService.generateAccessToken(user)
                );
            }

            refreshTokenRepository.revokeAllByFamilyId(refreshToken.getFamilyId());
            emailService.sendSecurityAlert(user.getEmail());
            throw new TokenReuseException();
        }

        if (refreshToken.isExpired()) {
            refreshToken.revoke();
            refreshTokenRepository.save(refreshToken);
            throw new ExpiredTokenException();
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
            long secondsSinceLastEmail = Duration.between(
                    existingToken.get().getCreatedAt(), Instant.now()).getSeconds();
            long cooldownSeconds = authProps.token().cooldownPeriodMs() / 1000;

            if (secondsSinceLastEmail < cooldownSeconds) {
                throw new EmailCooldownException(cooldownSeconds - secondsSinceLastEmail);
            }

            resetPasswordTokenRepository.delete(existingToken.get());
        }

        ResetPasswordToken token = new ResetPasswordToken(
                jwtService.generateOpaqueToken(),
                user,
                Instant.now().plusMillis(authProps.token().resetPasswordExpirationMs())
        );

        try {
            token = resetPasswordTokenRepository.saveAndFlush(token);
            emailService.sendPasswordResetEmail(user.getEmail(), token.getToken());
        } catch (DataIntegrityViolationException ignored) {
            log.warn("Concurrent reset password token creation, token exists");
        }
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequestDto resetPasswordRequestDto) {
        ResetPasswordToken token = resetPasswordTokenRepository.findByToken(
                resetPasswordRequestDto.token()
        ).orElseThrow(InvalidTokenException::new);

        if (token.isExpired()) {
            resetPasswordTokenRepository.delete(token);
            throw new ExpiredTokenException();
        }

        User user = token.getUser();
        user.setPassword(
                passwordEncoder.encode(resetPasswordRequestDto.newPassword())
        );

        user = userRepository.save(user);
        refreshTokenRepository.revokeAllByUserId(user.getId());
        resetPasswordTokenRepository.delete(token);
    }

    @Override
    @Transactional
    public User registerEmployee(String email, String password) {
        VerifyToken token = registerWithRole(email, password, Role.EMPLOYEE);
        emailService.sendEmployeeVerificationEmail(email, token.getToken());
        return token.getUser();
    }

    @Override
    @Transactional
    public void verifyEmployee(String token) {
        verifyUser(token);
    }

    private User verifyUser(String token) {
        VerifyToken verifyToken = verifyTokenRepository.findByToken(token)
                .orElseThrow(InvalidTokenException::new);

        if (verifyToken.isExpired()) {
            verifyTokenRepository.delete(verifyToken);
            throw new ExpiredTokenException();
        }

        User user = verifyToken.getUser();
        user.verify();

        user = userRepository.save(user);
        verifyTokenRepository.delete(verifyToken);
        return user;
    }

    private VerifyToken registerWithRole(String email, String password, Role role) {
        User user;
        try {
            user = userRepository.saveAndFlush(new User(
                    email,
                    passwordEncoder.encode(password),
                    role
            ));
        } catch (DataIntegrityViolationException ex) {
            throw new EmailTakenException();
        }

        VerifyToken token = new VerifyToken(
                jwtService.generateOpaqueToken(),
                user,
                Instant.now().plusMillis(authProps.token().verifyExpirationMs())
        );

        return verifyTokenRepository.save(token);
    }

    private String generateNewRefreshToken(User user) {
        RefreshToken refreshToken = refreshTokenRepository.save(new RefreshToken(
                jwtService.generateOpaqueToken(),
                user,
                Instant.now().plusMillis(authProps.jwt().refreshToken().expirationMs())
        ));

        return refreshToken.getToken();
    }
}
