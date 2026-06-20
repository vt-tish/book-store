package com.vttish.bookstore.cart.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public record CartResponseDto(
        List<CartItemDto> cartItems,
        BigDecimal totalPrice
) {
    public static CartResponseDto empty() {
        return new CartResponseDto(new ArrayList<>(), BigDecimal.ZERO);
    }
}
