package com.vttish.bookstore.books.dto;

import com.vttish.bookstore.books.entity.enums.AgeGroup;
import com.vttish.bookstore.books.entity.enums.Language;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public record AdminBookDetailsResponseDto(
        UUID id,
        String previewUrl,
        AgeGroup ageGroup,
        BigDecimal price,
        LocalDate publicationDate,
        Integer pages,
        Language language,
        Instant createdAt,
        Instant updatedAt,
        boolean isArchived,
        Map<String, BookTranslationDto> translations
) {}
