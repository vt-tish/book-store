package com.vttish.book_store.books.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BookCardDto(
        UUID id,
        String name,
        String author,
        String genre,
        BigDecimal price,
        String previewUrl
) {}
