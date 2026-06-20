package com.vttish.bookstore.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyRequestDto(

        @NotBlank(message = "Token is required")
        String token
) {}
