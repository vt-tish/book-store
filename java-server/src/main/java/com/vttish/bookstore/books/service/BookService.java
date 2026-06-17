package com.vttish.bookstore.books.service;

import com.vttish.bookstore.books.dto.AdminBookCardDto;
import com.vttish.bookstore.books.dto.AdminBookDetailsDto;
import com.vttish.bookstore.books.dto.BookCardDto;
import com.vttish.bookstore.books.dto.BookDetailsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface BookService {
    Page<BookCardDto> getAvailable(Pageable pageable);
    Page<AdminBookCardDto> getAll(Pageable pageable);
    BookDetailsDto getById(UUID id);
    AdminBookDetailsDto getByIdAdmin(UUID id);

    Optional<BigDecimal> getPriceById(UUID id);
    Map<UUID, BigDecimal> getPricesByIds(List<UUID> bookIds);
}
