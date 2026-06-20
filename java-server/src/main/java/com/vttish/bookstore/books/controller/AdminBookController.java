package com.vttish.bookstore.books.controller;

import com.vttish.bookstore.books.dto.AdminBookCardResponseDto;
import com.vttish.bookstore.books.dto.AdminBookDetailsResponseDto;
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
            @Valid @RequestBody BookRequestDto bookRequestDto
    ) {
        AdminBookDetailsResponseDto createdBook = bookManagementService.create(bookRequestDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdBook.id())
                .toUri();

        return ResponseEntity.created(location).body(createdBook);
    }

    @GetMapping
    public Page<AdminBookCardResponseDto> getAll(Pageable pageable) {
        return bookQueryService.getAll(pageable);
    }

    @GetMapping("{id}")
    public AdminBookDetailsResponseDto getById(@PathVariable UUID id) {
        return bookQueryService.getByIdAdmin(id);
    }

    @PutMapping("/{id}")
    public AdminBookDetailsResponseDto update(
            @PathVariable UUID id, @Valid @RequestBody BookRequestDto bookRequestDto
    ) {
        return bookManagementService.update(id, bookRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        bookManagementService.delete(id);
    }

    @PatchMapping("/{id}/archive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archive(@PathVariable UUID id) {
        bookManagementService.archive(id);
    }

    @PatchMapping("/{id}/unarchive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unarchive(@PathVariable UUID id) {
        bookManagementService.unarchive(id);
    }
}
