package com.vttish.bookstore.cart.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemDto(
        UUID bookId,
        String bookName,
        Integer quantity,
        BigDecimal pricePerUnit,
        BigDecimal subtotalPrice,
        boolean isAvailable
) {}
