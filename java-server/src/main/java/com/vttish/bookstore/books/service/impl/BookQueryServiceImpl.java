package com.vttish.bookstore.books.service.impl;

import com.vttish.bookstore.books.dto.*;
import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.mapper.BookMapper;
import com.vttish.bookstore.books.repository.BookRepository;
import com.vttish.bookstore.books.service.BookQueryService;
import com.vttish.bookstore.common.exception.EntityNotFoundException;
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
public class BookQueryServiceImpl implements BookQueryService {
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
    public Map<UUID, CartBookView> getBooksForCart(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Map.of();
        }

        Set<CartBookView> prices = bookRepository.findByIdInAndIsArchivedFalse(ids, CartBookView.class);

        return prices.stream()
                .collect(Collectors.toMap(CartBookView::getId, view -> view));
    }

    @Override
    public Map<UUID, OrderBookView> getBooksForOrder(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Map.of();
        }

        Set<OrderBookView> bookViews = bookRepository.findByIdInAndIsArchivedFalse(ids, OrderBookView.class);

        return bookViews.stream()
                .collect(Collectors.toMap(OrderBookView::getId, view -> view));
    }

    private Book getEntityById(UUID id) {
        return bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(Book.class, id)
        );
    }
}
