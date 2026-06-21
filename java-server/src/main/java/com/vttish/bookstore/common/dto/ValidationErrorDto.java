package com.vttish.bookstore.common.dto;

public record ValidationErrorDto(
        String field,
        String message
) {}
