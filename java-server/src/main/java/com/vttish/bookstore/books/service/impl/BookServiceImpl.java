package com.vttish.bookstore.books.service.impl;

import com.vttish.bookstore.books.dto.*;
import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.mapper.BookMapper;
import com.vttish.bookstore.books.repository.BookRepository;
import com.vttish.bookstore.books.service.BookService;
import com.vttish.bookstore.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper mapper;

    @Override
    public Page<BookCardDto> getAvailable(Pageable pageable) {
        return bookRepository.findAllByIsArchivedFalse(pageable).map(mapper::toBookCardDto);
    }

    @Override
    public Page<AdminBookCardDto> getAll(Pageable pageable) {
        return bookRepository.findAll(pageable).map(mapper::toAdminBookCardDto);
    }

    @Override
    public BookDetailsDto getById(UUID id) {
        return mapper.toBookDetailsDto(getEntityById(id));
    }

    @Override
    public AdminBookDetailsDto getByIdAdmin(UUID id) {
        return mapper.toAminBookDetailsDto(getEntityById(id));
    }

    @Override
    public CartBookView getAvailableByIdView(UUID id) {
        return bookRepository.findByIdAndIsArchivedFalse(id).orElseThrow(() ->
                new NotFoundException(Book.class, id)
        );
    }

    @Override
    public Map<UUID, BigDecimal> getPricesByIds(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Map.of();
        }

        Set<BookPriceView> prices = bookRepository.findByIdInAndIsArchivedFalse(ids);

        return prices.stream()
                .collect(Collectors.toMap(BookPriceView::getId, BookPriceView::getPrice));
    }

    private Book getEntityById(UUID id) {
        return bookRepository.findById(id).orElseThrow(
                () -> new NotFoundException(Book.class, id)
        );
    }
}
