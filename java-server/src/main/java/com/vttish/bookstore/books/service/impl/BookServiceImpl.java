package com.vttish.bookstore.books.service.impl;

import com.vttish.bookstore.books.dto.BookCardDto;
import com.vttish.bookstore.books.dto.BookDetailsDto;
import com.vttish.bookstore.books.dto.BookDto;
import com.vttish.bookstore.books.dto.BookPriceView;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper mapper;

    @Override
    @Transactional
    public BookDetailsDto create(BookDto bookDto) {
        Book book = mapper.toBook(bookDto);
        return mapper.toBookDetailsDto(bookRepository.save(book));
    }

    @Override
    public Page<BookCardDto> getAll(Pageable pageable) {
        return bookRepository.findAll(pageable).map(mapper::toBookCardDto);
    }

    private Book getEntityById(UUID id) {
        return bookRepository.findById(id).orElseThrow(() -> new NotFoundException(Book.class, id));
    }

    @Override
    public BookDetailsDto getById(UUID id) {
        return mapper.toBookDetailsDto(getEntityById(id));
    }

    @Override
    @Transactional
    public BookDetailsDto update(UUID id, BookDto bookDto) {
        Book book = getEntityById(id);

        mapper.update(bookDto, book);
        return mapper.toBookDetailsDto(bookRepository.save(book));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!bookRepository.existsById(id)) {
            throw new NotFoundException(Book.class, id);
        }

        bookRepository.deleteById(id);
    }

    @Override
    public Map<UUID, BigDecimal> getPricesByIds(List<UUID> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) {
            return Map.of();
        }

        List<BookPriceView> prices = bookRepository.findByIdIn(bookIds);

        return prices.stream()
                .collect(Collectors.toMap(BookPriceView::getId, BookPriceView::getPrice));
    }
}
