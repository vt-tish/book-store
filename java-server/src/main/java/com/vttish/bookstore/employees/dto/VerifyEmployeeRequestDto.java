package com.vttish.bookstore.employees.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyEmployeeRequestDto(

        @NotBlank(message = "{error.validation.required}")
        String token
) {}
