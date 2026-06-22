package com.vttish.bookstore.books.dto;

import com.vttish.bookstore.books.entity.enums.AgeGroup;
import com.vttish.bookstore.books.entity.enums.Language;
import com.vttish.bookstore.common.validation.ImageUrl;
import com.vttish.bookstore.common.validation.SupportedTranslations;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record BookRequestDto(

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

        @NotNull(message = "{error.validation.required}")
        @Min(value = 5, message = "{error.validation.min_value}")
        Integer pages,

        @NotNull(message = "{error.validation.required}")
        Language language,

        @SupportedTranslations
        Map<String, @Valid BookTranslationDto> translations
) {}
