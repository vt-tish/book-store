package com.vttish.bookstore.orders.dto;

import com.vttish.bookstore.orders.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderDetailsResponseDto(
        UUID id,
        BigDecimal totalPrice,
        OrderStatus status,
        Instant createdAt,
        Instant closedAt,
        List<OrderItemDto> items
) {}
