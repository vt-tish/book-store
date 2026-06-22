package com.vttish.bookstore.employees.dto;

import com.vttish.bookstore.common.validation.Email;
import com.vttish.bookstore.common.validation.Phone;
import com.vttish.bookstore.common.validation.StrongPassword;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegisterEmployeeRequestDto(

        @NotBlank(message = "{error.validation.required}")
        @Email(message = "{error.validation.email}")
        String email,

        @NotBlank(message = "{error.validation.required}")
        @Size(min = 8, message = "{error.validation.min_length}")
        @StrongPassword(message = "{error.validation.strong_password}")
        String password,

        @NotBlank(message = "{error.validation.required}")
        @Phone(message = "{error.validation.phone}")
        String phone,

        @NotNull(message = "{error.validation.required}")
        @Past(message = "{error.validation.past}")
        LocalDate birthDate
) {}
