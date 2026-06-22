package com.vttish.bookstore.employees.dto;

import com.vttish.bookstore.common.validation.Phone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record UpdateEmployeeRequestDto(

        @NotBlank(message = "{error.validation.required}")
        @Phone(message = "{error.validation.phone}")
        String phone,

        @NotNull(message = "{error.validation.required}")
        @Past(message = "{error.validation.past}")
        LocalDate birthDate
) {}
