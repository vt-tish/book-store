package com.vttish.bookstore.books.dto;

import com.vttish.bookstore.books.entity.enums.AgeGroup;
import com.vttish.bookstore.books.entity.enums.Language;
import com.vttish.bookstore.common.validation.ImageUrl;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookRequestDto(
        @NotBlank(message = "{error.validation.required}")
        @Size(max = 255, message = "{error.validation.max_length}")
        String name,

        @NotBlank(message = "{error.validation.required}")
        @Size(max = 100, message = "{error.validation.max_length}")
        String genre,

        @Size(max = 2048, message = "{error.validation.max_length}")
        @ImageUrl(message = "{error.validation.image_url}")
        String previewUrl,

        @NotNull(message = "{error.validation.required}")
        AgeGroup ageGroup,

        @NotNull(message = "{error.validation.required}")
        @Positive(message = "{error.validation.positive}")
        @Digits(integer = 8, fraction = 2, message = "{error.validation.digits}")
        BigDecimal price,

        @NotNull(message = "{error.validation.required}")
        @PastOrPresent(message = "{error.validation.past_or_present}")
        LocalDate publicationDate,

        @NotBlank(message = "{error.validation.required}")
        @Size(max = 255, message = "{error.validation.max_length}")
        String author,

        @NotNull(message = "{error.validation.required}")
        @Min(value = 5, message = "{error.validation.min_value}")
        Integer pages,

        @Size(max = 2000, message = "{error.validation.max_length}")
        String characteristics,

        @Size(max = 5000, message = "{error.validation.max_length}")
        String description,

        @NotNull(message = "{error.validation.required}")
        Language language
) {}
