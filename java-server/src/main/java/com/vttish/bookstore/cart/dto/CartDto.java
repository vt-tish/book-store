package com.vttish.bookstore.cart.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public record CartDto(
        List<CartItemDto> cartItems,
        BigDecimal totalPrice
) {
    public static CartDto empty() {
        return new CartDto(new ArrayList<>(), BigDecimal.ZERO);
    }
}
