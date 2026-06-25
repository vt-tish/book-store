package com.vttish.bookstore.books.controller;

import com.vttish.bookstore.books.dto.BookCardResponseDto;
import com.vttish.bookstore.books.dto.BookDetailsResponseDto;
import com.vttish.bookstore.books.dto.BookFilterRequestDto;
import com.vttish.bookstore.books.service.BookQueryService;
import com.vttish.bookstore.common.constant.ApiRoutingConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping(ApiRoutingConstants.API_V1 + "/books")
@Validated
@RequiredArgsConstructor
public class BookController {
    private final BookQueryService bookQueryService;

    @GetMapping
    public Page<BookCardResponseDto> getAvailable(
            @Valid BookFilterRequestDto filter,
            Pageable pageable,
            Locale locale
    ) {
        return bookQueryService.getAvailable(filter, locale.getLanguage(), pageable);
    }

    @GetMapping("/{id}")
    public BookDetailsResponseDto getById(@PathVariable UUID id, Locale locale) {
        return bookQueryService.getById(id, locale.getLanguage());
    }
}
