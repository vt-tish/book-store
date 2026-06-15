package com.vttish.bookstore.books.service;

import com.vttish.bookstore.books.dto.BookCardDto;
import com.vttish.bookstore.books.dto.BookDetailsDto;
import com.vttish.bookstore.books.dto.BookDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface BookService {
    BookDetailsDto create(BookDto bookDto);
    Page<BookCardDto> getAll(Pageable pageable);
    BookDetailsDto getById(UUID id);
    BookDetailsDto update(UUID id, BookDto bookDto);
    void delete(UUID id);

    Map<UUID, BigDecimal> getPricesByIds(List<UUID> bookIds);
}
