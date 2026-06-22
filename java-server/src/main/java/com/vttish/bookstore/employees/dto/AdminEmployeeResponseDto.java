package com.vttish.bookstore.employees.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record AdminEmployeeResponseDto(
        UUID id,
        String email,
        String phone,
        LocalDate birthDate,
        boolean isBlocked,
        Instant createdAt
) {}
