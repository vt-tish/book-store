package com.vttish.bookstore.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDto(

        @NotBlank(message = "Email is required")
        // TODO: Add proper email validation annotation
        @Email(message = "Email is not valid")
        String email,

        @NotBlank(message = "Password is required")
        // TODO: @Password(minLength, includeCharTypes, message)
        String password
) {}
