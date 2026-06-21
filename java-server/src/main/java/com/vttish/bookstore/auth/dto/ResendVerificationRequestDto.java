package com.vttish.bookstore.auth.dto;

import jakarta.validation.constraints.Email;

public record ResendVerificationRequestDto(

        @Email(message = "Email is not valid")
        String email
) {}
