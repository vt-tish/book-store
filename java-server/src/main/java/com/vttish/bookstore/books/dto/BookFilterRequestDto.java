package com.vttish.bookstore.books.dto;

import com.vttish.bookstore.common.validation.PriceRange;
import com.vttish.bookstore.common.validation.ValidPriceRange;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@ValidPriceRange(message = "{error.validation.valid_price_range}")
public record BookFilterRequestDto(
        String search,

        @PositiveOrZero(message = "{error.validation.positive_or_zero}")
        @Digits(integer = 8, fraction = 2, message = "{error.validation.digits}")
        BigDecimal minPrice,

        @PositiveOrZero(message = "{error.validation.positive_or_zero}")
        @Digits(integer = 8, fraction = 2, message = "{error.validation.digits}")
        BigDecimal maxPrice
) implements PriceRange {}
