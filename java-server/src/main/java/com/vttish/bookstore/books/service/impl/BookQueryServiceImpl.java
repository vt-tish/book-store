package com.vttish.bookstore.books.service.impl;

import com.vttish.bookstore.books.dto.*;
import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.exception.BookNotFoundException;
import com.vttish.bookstore.books.mapper.BookMapper;
import com.vttish.bookstore.books.mapper.BookSpecifications;
import com.vttish.bookstore.books.repository.BookRepository;
import com.vttish.bookstore.books.service.BookQueryService;
import com.vttish.bookstore.common.config.LocalizationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookQueryServiceImpl implements BookQueryService {
    private final BookRepository bookRepository;
    private final LocalizationProperties localizationProps;
    private final BookMapper mapper;

    @Override
    public Page<BookCardResponseDto> getAvailable(String search, String lang, Pageable pageable) {
        return bookRepository.findAll(
                BookSpecifications.searchAvailable(search),
                pageable
        ).map(book -> mapper.toBookCard(book, lang, localizationProps.defaultLanguage()));
    }

    @Override
    public Page<AdminBookCardResponseDto> getAll(String search, String lang, Pageable pageable) {
        return bookRepository.findAll(
                BookSpecifications.search(search),
                pageable
        ).map(book -> mapper.toAdminBookCard(book, lang, localizationProps.defaultLanguage()));
    }

    @Override
    public BookDetailsResponseDto getById(UUID id, String lang) {
        return bookRepository.findByIdDetails(
                id, localizationProps.resolveLanguage(lang)
        ).orElseThrow(BookNotFoundException::new);
    }

    @Override
    public AdminBookDetailsResponseDto getByIdAdmin(UUID id, String lang) {
        return bookRepository.findByIdAdminDetails(
                id, localizationProps.resolveLanguage(lang)
        ).orElseThrow(BookNotFoundException::new);
    }

    @Override
    public Book getByIdAvailable(UUID id) {
        return bookRepository.findByIdAndIsArchivedFalse(id).orElseThrow(
                BookNotFoundException::new
        );
    }
}
