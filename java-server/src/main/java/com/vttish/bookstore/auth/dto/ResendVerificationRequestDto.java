package com.vttish.bookstore.auth.dto;

import com.vttish.bookstore.common.validation.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendVerificationRequestDto(

        @NotBlank(message = "{error.validation.required}")
        @Email(message = "{error.validation.email}")
        String email
) {}
