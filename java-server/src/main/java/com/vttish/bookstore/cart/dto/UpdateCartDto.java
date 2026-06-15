package com.vttish.bookstore.cart.dto;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public record UpdateCartDto(
        @Valid
        List<AddCartItemDto> cartItems,

        List<UUID> excludeBookIds
) {
    public boolean areUpdatableFieldsEmpty() {
        return (cartItems == null || cartItems.isEmpty())
                && (excludeBookIds == null || excludeBookIds.isEmpty());
    }
}
