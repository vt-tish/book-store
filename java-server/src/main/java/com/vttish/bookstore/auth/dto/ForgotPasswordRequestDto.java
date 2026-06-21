package com.vttish.bookstore.auth.dto;

import jakarta.validation.constraints.Email;

public record ForgotPasswordRequestDto(

        @Email(message = "Email is not valid")
        String email
) {}
