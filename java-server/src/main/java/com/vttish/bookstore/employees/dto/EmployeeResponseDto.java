package com.vttish.bookstore.employees.dto;

import java.time.LocalDate;

public record EmployeeResponseDto(
        String email,
        String phone,
        LocalDate birthDate
) {}
