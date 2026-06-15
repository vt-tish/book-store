package com.vttish.bookstore.books.dto;

import com.vttish.bookstore.books.entity.enums.AgeGroup;
import com.vttish.bookstore.books.entity.enums.Language;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookDto(
        @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Max book name length is 255 characters")
        String name,

        @NotBlank(message = "Genre is required")
        @Size(max = 100, message = "Max genre name length is 100 characters")
        String genre,

        @Size(max = 2048, message = "Max preview URL length is 2048 characters")
        @Pattern(regexp = "^(http|https)://.*$", message = "Preview URL must be a valid HTTP/HTTPS link")
        String previewUrl,

        @NotNull(message = "Age group is required")
        AgeGroup ageGroup,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than zero")
        @Digits(integer = 8, fraction = 2, message = "Price must be a valid monetary amount")
        BigDecimal price,

        @NotNull(message = "Publication date is required")
        @PastOrPresent(message = "Publication date cannot be in the future")
        LocalDate publicationDate,

        @NotBlank(message = "Author is required")
        @Size(max = 255, message = "Max author name length is 255 characters")
        String author,

        @NotNull(message = "Number of pages is required")
        @Min(value = 5, message = "Min number of pages must is 5")
        Integer pages,

        @Size(max = 2000, message = "Max characteristics length is 2000 characters")
        String characteristics,

        @Size(max = 5000, message = "Max description length is 5000 characters")
        String description,

        @NotNull(message = "Language is required")
        Language language
) {}
