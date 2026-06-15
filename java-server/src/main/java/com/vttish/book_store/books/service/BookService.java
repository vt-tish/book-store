package com.vttish.book_store.books.service;

import com.vttish.book_store.books.dto.BookCardDto;
import com.vttish.book_store.books.dto.BookDetailsDto;
import com.vttish.book_store.books.dto.BookDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BookService {
    BookDetailsDto create(BookDto bookDto);
    Page<BookCardDto> getAll(Pageable pageable);
    BookDetailsDto getById(UUID id);
    BookDetailsDto update(UUID id, BookDto bookDto);
    void delete(UUID id);
}
