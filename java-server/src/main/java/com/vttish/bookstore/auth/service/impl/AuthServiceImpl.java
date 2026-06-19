package com.vttish.bookstore.auth.service.impl;

import com.vttish.bookstore.auth.dto.LoginDto;
import com.vttish.bookstore.auth.dto.RegisterDto;
import com.vttish.bookstore.auth.dto.TokensDto;
import com.vttish.bookstore.auth.entity.RefreshToken;
import com.vttish.bookstore.auth.entity.User;
import com.vttish.bookstore.auth.entity.enums.Role;
import com.vttish.bookstore.auth.repository.RefreshTokenRepository;
import com.vttish.bookstore.auth.repository.UserRepository;
import com.vttish.bookstore.auth.service.AuthService;
import com.vttish.bookstore.auth.service.JwtService;
import com.vttish.bookstore.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${application.security.jwt.refresh-token.expiration-ms}")
    private Long refreshTokenExpirationMs;

    @Value("${application.security.jwt.refresh-token.grace-period-ms}")
    private Long gracePeriodMs;

    @Override
    @Transactional
    public TokensDto register(RegisterDto registerDto) {
        if (userRepository.findByEmail(registerDto.email()).isPresent()) {
            throw new BadRequestException("Email is already in use");
        }

        User user = userRepository.save(new User(
                registerDto.email(),
                passwordEncoder.encode(registerDto.password()),
                Role.CLIENT
        ));

        return new TokensDto(generateNewRefreshToken(user), jwtService.generateAccessToken(user));
    }

    @Override
    @Transactional
    public TokensDto login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.email()).orElseThrow(() ->
                new BadCredentialsException("Invalid email or password")
        );

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
                    refreshToken.getConsumedAt().plusMillis(gracePeriodMs).isAfter(Instant.now())
            ) {
                return new TokensDto(
                        refreshToken.getChildToken(),
                        jwtService.generateAccessToken(user)
                );
            }

            revokeRefreshTokenFamily(refreshToken.getFamilyId());
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

    public String generateNewRefreshToken(User user) {
        RefreshToken refreshToken = refreshTokenRepository.save(new RefreshToken(
                jwtService.generateOpaqueToken(),
                user,
                Instant.now().plusMillis(refreshTokenExpirationMs)
        ));

        return refreshToken.getToken();
    }

    private void revokeRefreshTokenFamily(UUID familyId) {
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAllByFamilyId(familyId);

        if (refreshTokens.isEmpty()) {
            return;
        }

        refreshTokens.forEach(RefreshToken::revoke);
        refreshTokenRepository.saveAll(refreshTokens);
    }
}
