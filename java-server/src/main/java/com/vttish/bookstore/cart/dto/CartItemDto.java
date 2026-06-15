package com.vttish.bookstore.cart.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemDto(
        UUID bookId,
        Integer quantity,
        BigDecimal pricePerUnit,
        BigDecimal oldPricePerUnit,
        BigDecimal subtotalPrice
) {}
