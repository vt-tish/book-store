package com.vttish.bookstore.cart.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemDto(
        UUID id,
        UUID bookId,
        String bookName,
        String previewUrl,
        Integer quantity,
        BigDecimal pricePerUnit,
        BigDecimal subtotalPrice,
        boolean isAvailable
) {}
