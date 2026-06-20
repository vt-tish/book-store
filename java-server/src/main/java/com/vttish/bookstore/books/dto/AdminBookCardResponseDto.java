package com.vttish.bookstore.books.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AdminBookCardResponseDto(
        UUID id,
        String name,
        String author,
        String genre,
        BigDecimal price,
        String previewUrl,
        boolean isArchived
) {}
