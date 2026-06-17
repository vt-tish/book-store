package com.vttish.bookstore.books.controller;

import com.vttish.bookstore.books.dto.AdminBookCardDto;
import com.vttish.bookstore.books.dto.AdminBookDetailsDto;
import com.vttish.bookstore.books.dto.BookDetailsDto;
import com.vttish.bookstore.books.dto.BookDto;
import com.vttish.bookstore.books.service.BookManagementService;
import com.vttish.bookstore.books.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/v1/admin/books")
@Validated
@RequiredArgsConstructor
public class AdminBookController {
    private final BookManagementService bookManagementService;
    private final BookService bookService;

    @PostMapping
    public ResponseEntity<AdminBookDetailsDto> create(@Valid @RequestBody BookDto bookDto) {
        AdminBookDetailsDto createdBook = bookManagementService.create(bookDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdBook.id())
                .toUri();

        return ResponseEntity.created(location).body(createdBook);
    }

    @GetMapping
    public Page<AdminBookCardDto> getAll(Pageable pageable) {
        return bookService.getAll(pageable);
    }

    @GetMapping("{id}")
    public AdminBookDetailsDto getById(@PathVariable UUID id) {
        return bookService.getByIdAdmin(id);
    }

    @PutMapping("/{id}")
    public AdminBookDetailsDto update(@PathVariable UUID id, @Valid @RequestBody BookDto bookDto) {
        return bookManagementService.update(id, bookDto);
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
