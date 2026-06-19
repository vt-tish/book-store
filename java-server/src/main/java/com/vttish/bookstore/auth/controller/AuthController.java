package com.vttish.bookstore.auth.controller;

import com.vttish.bookstore.auth.dto.AuthResponseDto;
import com.vttish.bookstore.auth.dto.LoginDto;
import com.vttish.bookstore.auth.dto.RegisterDto;
import com.vttish.bookstore.auth.dto.TokensDto;
import com.vttish.bookstore.auth.service.AuthService;
import com.vttish.bookstore.auth.service.CookieService;
import com.vttish.bookstore.common.constant.ApiRoutingConstants;
import com.vttish.bookstore.common.constant.CookieConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiRoutingConstants.API_V1 + "/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CookieService cookieService;

    private static final String REFRESH_TOKEN_COOKIE_PATH = ApiRoutingConstants.API_V1 + "/auth/refresh";

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterDto registerDto) {
        TokensDto tokens = authService.register(registerDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(cookieService.createRefreshTokenCookie(tokens.refreshToken(), REFRESH_TOKEN_COOKIE_PATH))
                .body(new AuthResponseDto(tokens.accessToken()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
        TokensDto tokens = authService.login(loginDto);

        return ResponseEntity.ok()
                .headers(cookieService.createRefreshTokenCookie(tokens.refreshToken(), REFRESH_TOKEN_COOKIE_PATH))
                .body(new AuthResponseDto(tokens.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(CookieConstants.REFRESH_TOKEN) String refreshToken
    ) {
        authService.logout(refreshToken);

        return ResponseEntity.noContent()
                .headers(cookieService.clearRefreshTokenCookie(REFRESH_TOKEN_COOKIE_PATH))
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(
            @CookieValue(CookieConstants.REFRESH_TOKEN) String refreshToken
    ) {
        TokensDto tokens = authService.refresh(refreshToken);

        return ResponseEntity.ok()
                .headers(cookieService.createRefreshTokenCookie(tokens.refreshToken(), REFRESH_TOKEN_COOKIE_PATH))
                .body(new AuthResponseDto(tokens.accessToken()));
    }
}
