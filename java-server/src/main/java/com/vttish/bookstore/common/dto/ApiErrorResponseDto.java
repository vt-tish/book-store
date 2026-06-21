package com.vttish.bookstore.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponseDto(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<ValidationErrorDto> validationErrors
) {
}
