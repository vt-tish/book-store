package com.vttish.bookstore.auth.controller;

import com.vttish.bookstore.auth.dto.*;
import com.vttish.bookstore.auth.service.AuthService;
import com.vttish.bookstore.auth.service.CookieService;
import com.vttish.bookstore.common.constant.ApiRoutingConstants;
import com.vttish.bookstore.common.constant.CookieConstants;
import com.vttish.bookstore.common.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping(ApiRoutingConstants.API_V1 + "/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CookieService cookieService;
    private final MessageSource messageSource;

    private static final String AUTH_COOKIE_PATH = ApiRoutingConstants.API_V1 + "/auth";

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto register(
            @Valid @RequestBody RegisterRequestDto registerRequestDto,
            Locale locale
    ) {
        authService.register(registerRequestDto);
        return new ResponseDto(
                messageSource.getMessage("success.auth.register", null, locale)
        );
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
            @Valid @RequestBody ResendVerificationRequestDto resendVerificationRequestDto,
            Locale locale
    ) {
        authService.resendVerification(resendVerificationRequestDto);
        return new ResponseDto(
                messageSource.getMessage("success.auth.resend_verification", null, locale)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        TokensDto tokens = authService.login(loginRequestDto);
        return ResponseEntity.ok()
                .headers(cookieService.createRefreshTokenCookie(tokens.refreshToken(), AUTH_COOKIE_PATH))
                .body(new AuthResponseDto(tokens.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDto> logout(
            @CookieValue(CookieConstants.REFRESH_TOKEN) String refreshToken,
            Locale locale
    ) {
        authService.logout(refreshToken);
        return ResponseEntity.ok()
                .headers(cookieService.clearRefreshTokenCookie(AUTH_COOKIE_PATH))
                .body(new ResponseDto(
                        messageSource.getMessage("success.auth.logout", null, locale)
                ));
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
            @Valid @RequestBody ForgotPasswordRequestDto forgotPasswordRequestDto,
            Locale locale
    ) {
        authService.forgotPassword(forgotPasswordRequestDto);
        return new ResponseDto(
                messageSource.getMessage("success.auth.forgot_password", null, locale)
        );
    }

    @PostMapping("/reset-password")
    public ResponseDto resetPassword(
            @Valid @RequestBody ResetPasswordRequestDto resetPasswordRequestDto,
            Locale locale
    ) {
        authService.resetPassword(resetPasswordRequestDto);
        return new ResponseDto(
                messageSource.getMessage("success.auth.reset_password", null, locale)
        );
    }
}
