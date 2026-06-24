package com.vttish.bookstore.books.controller;

import com.vttish.bookstore.books.dto.AdminBookCardResponseDto;
import com.vttish.bookstore.books.dto.AdminBookDetailsResponseDto;
import com.vttish.bookstore.books.dto.BookFilterDto;
import com.vttish.bookstore.books.dto.BookRequestDto;
import com.vttish.bookstore.books.service.BookManagementService;
import com.vttish.bookstore.books.service.BookQueryService;
import com.vttish.bookstore.common.constant.ApiRoutingConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping(ApiRoutingConstants.API_V1 + "/admin/books")
@Validated
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
public class AdminBookController {
    private final BookManagementService bookManagementService;
    private final BookQueryService bookQueryService;

    @PostMapping
    public ResponseEntity<AdminBookDetailsResponseDto> create(
            @Valid @RequestBody BookRequestDto bookRequestDto,
            Locale locale
    ) {
        AdminBookDetailsResponseDto createdBook = bookManagementService.create(
                locale.getLanguage(), bookRequestDto
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdBook.id())
                .toUri();

        return ResponseEntity.created(location).body(createdBook);
    }

    @GetMapping
    public Page<AdminBookCardResponseDto> getAll(
            @Valid BookFilterDto filter,
            Pageable pageable,
            Locale locale
    ) {
        return bookQueryService.getAll(filter, locale.getLanguage(), pageable);
    }

    @GetMapping("/{id}")
    public AdminBookDetailsResponseDto getById(@PathVariable UUID id, Locale locale) {
        return bookQueryService.getByIdAdmin(id, locale.getLanguage());
    }

    @PutMapping("/{id}")
    public AdminBookDetailsResponseDto update(
            @PathVariable UUID id,
            @Valid @RequestBody BookRequestDto bookRequestDto,
            Locale locale
    ) {
        return bookManagementService.update(id, locale.getLanguage(), bookRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        bookManagementService.delete(id);
    }

    @PutMapping("/{id}/archive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archive(@PathVariable UUID id) {
        bookManagementService.archive(id);
    }

    @PutMapping("/{id}/unarchive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unarchive(@PathVariable UUID id) {
        bookManagementService.unarchive(id);
    }
}
