package com.vttish.bookstore.auth.controller;

import com.vttish.bookstore.auth.dto.*;
import com.vttish.bookstore.auth.service.AuthService;
import com.vttish.bookstore.auth.service.CookieService;
import com.vttish.bookstore.common.constant.ApiRoutingConstants;
import com.vttish.bookstore.common.constant.CookieConstants;
import com.vttish.bookstore.common.dto.ResponseDto;
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

    private static final String AUTH_COOKIE_PATH = ApiRoutingConstants.API_V1 + "/auth";

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto register(@Valid @RequestBody RegisterDto registerDto) {
        authService.register(registerDto);
        return new ResponseDto("Successfully registered, verification link sent to the email");
    }

    @PostMapping("/verify")
    public ResponseEntity<AuthResponseDto> verify(
            @Valid @RequestBody VerifyRequestDto verifyRequestDto
    ) {
        TokensDto tokens = authService.verify(verifyRequestDto);
        return ResponseEntity.ok()
                .headers(cookieService.createRefreshTokenCookie(tokens.refreshToken(), AUTH_COOKIE_PATH))
                .body(new AuthResponseDto(tokens.accessToken()));
    }

    @PostMapping("/resend-verification")
    public ResponseDto resendVerification(
            @Valid @RequestBody ResendVerificationRequestDto resendVerificationRequestDto
    ) {
        authService.resendVerification(resendVerificationRequestDto);
        return new ResponseDto("Verification link resent to the email");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
        TokensDto tokens = authService.login(loginDto);
        return ResponseEntity.ok()
                .headers(cookieService.createRefreshTokenCookie(tokens.refreshToken(), AUTH_COOKIE_PATH))
                .body(new AuthResponseDto(tokens.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(CookieConstants.REFRESH_TOKEN) String refreshToken
    ) {
        authService.logout(refreshToken);
        return ResponseEntity.noContent()
                .headers(cookieService.clearRefreshTokenCookie(AUTH_COOKIE_PATH))
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(
            @CookieValue(CookieConstants.REFRESH_TOKEN) String refreshToken
    ) {
        TokensDto tokens = authService.refresh(refreshToken);
        return ResponseEntity.ok()
                .headers(cookieService.createRefreshTokenCookie(tokens.refreshToken(), AUTH_COOKIE_PATH))
                .body(new AuthResponseDto(tokens.accessToken()));
    }

    @PostMapping("/forgot-password")
    public ResponseDto forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDto forgotPasswordRequestDto
    ) {
        authService.forgotPassword(forgotPasswordRequestDto);
        return new ResponseDto("Reset password link sent to the email");
    }

    @PostMapping("/reset-password")
    public ResponseDto resetPassword(
            @Valid @RequestBody ResetPasswordRequestDto resetPasswordRequestDto
    ) {
        authService.resetPassword(resetPasswordRequestDto);
        return new ResponseDto("Password is reset successfully");
    }
}
