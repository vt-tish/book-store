package com.vttish.bookstore.auth.dto;

import com.vttish.bookstore.common.validation.StrongPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDto(

        @NotBlank(message = "{error.validation.required}")
        String token,

        @NotBlank(message = "{error.validation.required}")
        @Size(min = 8, message = "{error.validation.min_length}")
        @StrongPassword(message = "{error.validation.strong_password}")
        String newPassword
) {}
