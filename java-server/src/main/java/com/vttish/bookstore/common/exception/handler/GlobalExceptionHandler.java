package com.vttish.bookstore.common.exception.handler;

import com.vttish.bookstore.common.dto.ApiErrorResponseDto;
import com.vttish.bookstore.common.dto.ValidationErrorDto;
import com.vttish.bookstore.common.exception.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final MessageSource messageSource;

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiErrorResponseDto> handleBaseExceptions(
            BaseException ex,
            HttpServletRequest request,
            Locale locale
    ) {
        ApiErrorResponseDto response = new ApiErrorResponseDto(
                Instant.now(),
                ex.getStatus().value(),
                ex.getStatus().getReasonPhrase(),
                messageSource.getMessage(ex.getMessage(), ex.getArgs(), locale),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiErrorResponseDto handleAccessDeniedExceptions(
            HttpServletRequest request,
            Locale locale
    ) {
        return new ApiErrorResponseDto(
                Instant.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                messageSource.getMessage("error.common.access_denied", null, locale),
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponseDto handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request,
            Locale locale
    ) {
        List<ValidationErrorDto> validationErrors = ex.getFieldErrors().stream()
                .map(error -> new ValidationErrorDto(
                        error.getField(),
                        messageSource.getMessage(error, locale)
                ))
                .toList();

        return new ApiErrorResponseDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                messageSource.getMessage("error.common.validation_failed", null, locale),
                request.getRequestURI(),
                validationErrors
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponseDto handleHttpMessageNotReadableExceptions(
            HttpServletRequest request,
            Locale locale
    ) {
        return new ApiErrorResponseDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                messageSource.getMessage("error.common.invalid_json", null, locale),
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void handleMethodNotSupported() {
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponseDto handleUnexpectedExceptions(
            HttpServletRequest request,
            Locale locale
    ) {
        return new ApiErrorResponseDto(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                messageSource.getMessage("error.common.internal_error", null, locale),
                request.getRequestURI(),
                null
        );
    }
}
