package com.vttish.bookstore.clients.dto;

import java.time.Instant;
import java.util.UUID;

public record AdminClientResponseDto(
        UUID id,
        String email,
        Integer ordersCount,
        boolean isBlocked,
        Instant createdAt
) {}
