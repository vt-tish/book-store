package com.vttish.bookstore.books.service.impl;

import com.vttish.bookstore.books.dto.AdminBookDetailsResponseDto;
import com.vttish.bookstore.books.dto.BookRequestDto;
import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.entity.BookTranslation;
import com.vttish.bookstore.books.exception.BookHasOrdersException;
import com.vttish.bookstore.books.exception.BookNotFoundException;
import com.vttish.bookstore.books.mapper.BookMapper;
import com.vttish.bookstore.books.repository.BookRepository;
import com.vttish.bookstore.books.service.BookManagementService;
import com.vttish.bookstore.common.config.LocalizationProperties;
import com.vttish.bookstore.orders.service.OrderQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookManagementServiceImpl implements BookManagementService {
    private final BookRepository bookRepository;
    private final OrderQueryService orderQueryService;
    private final LocalizationProperties localizationProperties;
    private final BookMapper mapper;

    @Override
    @Transactional
    public AdminBookDetailsResponseDto create(String lang, BookRequestDto bookRequestDto) {
        Book book = mapper.toBook(bookRequestDto);

        bookRequestDto.translations().forEach((langCode, translationDto) ->
                book.addTranslation(langCode, mapper.toBookTranslation(translationDto))
        );

        return mapper.toAdminBookDetails(
                bookRepository.save(book), lang, localizationProperties.defaultLanguage()
        );
    }

    @Override
    @Transactional
    public AdminBookDetailsResponseDto update(UUID id, String lang, BookRequestDto bookRequestDto) {
        Book book = getEntityById(id);

        mapper.update(bookRequestDto, book);

        bookRequestDto.translations().forEach((langCode, translationDto) -> {
            BookTranslation translation = book.getTranslations().get(langCode);

            if (translation != null) {
                mapper.updateTranslation(translationDto, translation);
            } else {
                book.addTranslation(langCode, mapper.toBookTranslation(translationDto));
            }
        });

        return mapper.toAdminBookDetails(
                bookRepository.save(book), lang, localizationProperties.defaultLanguage()
        );
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (orderQueryService.hasBookBeenOrdered(id)) {
            throw new BookHasOrdersException();
        }

        bookRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void archive(UUID id) {
        Book book = getEntityById(id);

        if (book.isArchived()) {
            return;
        }

        book.setArchived(true);
        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void unarchive(UUID id) {
        Book book = getEntityById(id);

        if (!book.isArchived()) {
            return;
        }

        book.setArchived(false);
        bookRepository.save(book);
    }

    private Book getEntityById(UUID id) {
        return bookRepository.findById(id).orElseThrow(BookNotFoundException::new);
    }
}
