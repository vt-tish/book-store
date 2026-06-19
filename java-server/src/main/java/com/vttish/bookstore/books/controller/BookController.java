package com.vttish.bookstore.books.controller;

import com.vttish.bookstore.books.dto.BookCardDto;
import com.vttish.bookstore.books.dto.BookDetailsDto;
import com.vttish.bookstore.books.service.BookQueryService;
import com.vttish.bookstore.common.constant.ApiRoutingConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiRoutingConstants.API_V1 + "/books")
@Validated
@RequiredArgsConstructor
public class BookController {
    private final BookQueryService bookQueryService;

    @GetMapping
    public Page<BookCardDto> getAvailable(Pageable pageable) {
        return bookQueryService.getAvailable(pageable);
    }

    @GetMapping("/{id}")
    public BookDetailsDto getById(@PathVariable UUID id) {
        return bookQueryService.getById(id);
    }
}
