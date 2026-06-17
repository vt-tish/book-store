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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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
    public Optional<BigDecimal> getPriceById(UUID id) {
        return bookRepository.findPriceByIdAndIsArchivedFalse(id);
    }

    @Override
    public Map<UUID, BigDecimal> getPricesByIds(List<UUID> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) {
            return Map.of();
        }

        List<BookPriceView> prices = bookRepository.findByIdInAndIsArchivedFalse(bookIds);

        return prices.stream()
                .collect(Collectors.toMap(BookPriceView::getId, BookPriceView::getPrice));
    }

    private Book getEntityById(UUID id) {
        return bookRepository.findById(id).orElseThrow(
                () -> new NotFoundException(Book.class, id)
        );
    }
}
