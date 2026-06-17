package com.vttish.bookstore.orders.dto;

import com.vttish.bookstore.orders.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderCardDto(
        UUID id,
        UUID clientId,
        UUID employeeId,
        BigDecimal totalPrice,
        Integer totalItems,
        OrderStatus status,
        LocalDateTime createdAt,
        LocalDateTime closedAt
) {}
