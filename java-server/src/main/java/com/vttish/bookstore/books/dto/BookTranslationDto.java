package com.vttish.bookstore.books.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BookTranslationDto(

        @NotBlank(message = "{error.validation.required}")
        @Size(max = 255, message = "{error.validation.max_length}")
        String name,

        @NotBlank(message = "{error.validation.required}")
        @Size(max = 100, message = "{error.validation.max_length}")
        String genre,

        @NotBlank(message = "{error.validation.required}")
        @Size(max = 255, message = "{error.validation.max_length}")
        String author,

        @Size(max = 2000, message = "{error.validation.max_length}")
        String characteristics,

        @Size(max = 5000, message = "{error.validation.max_length}")
        String description
) {}
