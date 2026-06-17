package com.vttish.bookstore.orders.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BookItemDto(
    UUID id,
    UUID bookId,
    String bookName,
    String bookAuthor,
    BigDecimal pricePerUnit,
    Integer quantity,
    BigDecimal subtotalPrice
) {}
