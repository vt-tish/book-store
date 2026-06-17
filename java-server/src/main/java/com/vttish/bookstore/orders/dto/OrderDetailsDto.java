package com.vttish.bookstore.orders.dto;

import com.vttish.bookstore.orders.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDetailsDto(
        UUID id,
        UUID clientId,
        UUID employeeId,
        BigDecimal totalPrice,
        OrderStatus status,
        LocalDateTime createdAt,
        LocalDateTime closedAt,
        List<BookItemDto> bookItems
) {}
