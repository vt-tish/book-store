package com.vttish.bookstore.books.service.impl;

import com.vttish.bookstore.books.dto.*;
import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.entity.BookTranslation;
import com.vttish.bookstore.books.exception.BookNotFoundException;
import com.vttish.bookstore.books.mapper.BookMapper;
import com.vttish.bookstore.books.repository.BookSpecifications;
import com.vttish.bookstore.books.repository.BookRepository;
import com.vttish.bookstore.books.repository.BookTranslationRepository;
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
    private final BookTranslationRepository bookTranslationRepository;
    private final LocalizationProperties localizationProps;
    private final BookMapper mapper;

    @Override
    public Page<BookCardResponseDto> getAvailable(BookFilterDto filter, String lang, Pageable pageable) {
        Page<Book> books = bookRepository.findAll(
                BookSpecifications.availableByFilter(filter),
                pageable
        );

        if (books.isEmpty()) {
            return Page.empty(pageable);
        }

        Map<UUID, BookTranslation> translations = getTranslations(books.getContent(), lang);
        return books.map(book -> mapper.toBookCardDto(book, translations.get(book.getId())));
    }

    @Override
    public Page<AdminBookCardResponseDto> getAll(BookFilterDto filter, String lang, Pageable pageable) {
        Page<Book> books = bookRepository.findAll(
                BookSpecifications.byFilter(filter),
                pageable
        );

        if (books.isEmpty()) {
            return Page.empty(pageable);
        }

        Map<UUID, BookTranslation> translations = getTranslations(books.getContent(), lang);
        return books.map(book -> mapper.toAdminBookCardDto(book, translations.get(book.getId())));
    }

    @Override
    public BookDetailsResponseDto getById(UUID id, String lang) {
        Book book = bookRepository.findById(id).orElseThrow(BookNotFoundException::new);
        return mapper.toBookDetailsDto(book, getTranslation(book, lang));
    }

    @Override
    public AdminBookDetailsResponseDto getByIdAdmin(UUID id, String lang) {
        Book book = bookRepository.findById(id).orElseThrow(BookNotFoundException::new);
        return mapper.toAdminBookDetailsDto(book, getTranslation(book, lang));
    }

    @Override
    public Book getAvailableById(UUID id) {
        return bookRepository.findByIdAndIsArchivedFalse(id).orElseThrow(
                BookNotFoundException::new
        );
    }

    @Override
    public Map<UUID, BookTranslation> getTranslations(List<Book> books, String lang) {
        String resolvedLang = localizationProps.resolveLanguage(lang);

        return bookTranslationRepository
                .findByBookInAndLanguageCodeIn(books, List.of(resolvedLang, localizationProps.defaultLanguage()))
                .stream().collect(Collectors.toMap(
                        translation -> translation.getBook().getId(),
                        translation -> translation,
                        (existing, replacement) ->
                                existing.getLanguageCode().equals(resolvedLang) ? existing : replacement
                ));
    }

    private BookTranslation getTranslation(Book book, String lang) {
        return getTranslations(List.of(book), lang).get(book.getId());
    }
}
