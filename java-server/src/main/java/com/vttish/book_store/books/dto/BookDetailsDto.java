package com.vttish.book_store.books.dto;

import com.vttish.book_store.books.entity.enums.AgeGroup;
import com.vttish.book_store.books.entity.enums.Language;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BookDetailsDto(
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
        Language language
) {}
