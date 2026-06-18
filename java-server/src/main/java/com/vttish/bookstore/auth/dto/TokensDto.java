package com.vttish.bookstore.auth.dto;

public record TokensDto(
        String refreshToken,
        String accessToken
) {}
