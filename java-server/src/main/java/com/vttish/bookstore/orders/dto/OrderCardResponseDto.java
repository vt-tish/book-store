package com.vttish.bookstore.orders.dto;

import com.vttish.bookstore.orders.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCardResponseDto(
        UUID id,
        UUID clientId,
        UUID employeeId,
        BigDecimal totalPrice,
        Integer totalItems,
        OrderStatus status,
        Instant createdAt,
        Instant closedAt
) {}
