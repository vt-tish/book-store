package com.vttish.bookstore.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequestDto(

        @NotBlank(message = "Token is required")
        String token,

        // TODO: @Password(minLength, includeCharTypes, message)
        String newPassword
) {}
