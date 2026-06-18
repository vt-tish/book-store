package com.vttish.bookstore.auth.service.impl;

import com.vttish.bookstore.auth.dto.AuthResponseDto;
import com.vttish.bookstore.auth.dto.LoginDto;
import com.vttish.bookstore.auth.dto.RegisterDto;
import com.vttish.bookstore.auth.dto.TokensDto;
import com.vttish.bookstore.auth.entity.User;
import com.vttish.bookstore.auth.repository.RefreshTokenRepository;
import com.vttish.bookstore.auth.repository.UserRepository;
import com.vttish.bookstore.auth.service.AuthService;
import com.vttish.bookstore.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public TokensDto register(RegisterDto registerDto) {
        return null;
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

        return null;
    }

    @Override
    @Transactional
    public AuthResponseDto refresh() {
        return null;
    }

    @Override
    @Transactional
    public void logout(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshToken -> {
            refreshToken.revoke();
            refreshTokenRepository.save(refreshToken);
        });
    }

    private TokensDto generateTokens(User user) {
        return null;
    }
}
