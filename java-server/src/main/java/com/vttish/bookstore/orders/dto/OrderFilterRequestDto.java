package com.vttish.bookstore.orders.dto;

import com.vttish.bookstore.common.validation.PriceRange;
import com.vttish.bookstore.common.validation.ValidPriceRange;
import com.vttish.bookstore.orders.entity.enums.OrderStatus;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

@ValidPriceRange
public record OrderFilterRequestDto(
        UUID clientId,
        UUID employeeId,
        OrderStatus status,

        @PositiveOrZero(message = "{error.validation.positive_or_zero}")
        @Digits(integer = 8, fraction = 2, message = "{error.validation.digits}")
        BigDecimal minPrice,

        @PositiveOrZero(message = "{error.validation.positive_or_zero}")
        @Digits(integer = 8, fraction = 2, message = "{error.validation.digits}")
        BigDecimal maxPrice
) implements PriceRange {}
