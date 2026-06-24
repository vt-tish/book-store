package com.vttish.bookstore.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateCartItemRequestDto(
        @NotNull(message = "{error.validation.required}")
        Integer quantity
) {}
