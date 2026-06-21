package com.vttish.bookstore.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddCartItemRequestDto(
        @NotNull(message = "{error.validation.required}")
        UUID bookId,

        @NotNull(message = "{error.validation.required}")
        @Min(value = 1, message = "{error.validation.min_value}")
        Integer quantity
) {}
