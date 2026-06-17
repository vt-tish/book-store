package com.vttish.bookstore.books.dto;

import com.vttish.bookstore.books.entity.enums.AgeGroup;
import com.vttish.bookstore.books.entity.enums.Language;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record AdminBookDetailsDto(
        UUID id,
        String name,
        String genre,
        String previewUrl,
        AgeGroup ageGroup,
        BigDecimal price,
        LocalDate publicationDate,
        String author,
        Integer pages,
        String characteristics,
        String description,
        Language language,
        Instant createdAt,
        Instant updatedAt,
        boolean isArchived
) {}
