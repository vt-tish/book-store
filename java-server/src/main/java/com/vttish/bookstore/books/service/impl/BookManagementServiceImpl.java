package com.vttish.bookstore.books.service.impl;

import com.vttish.bookstore.books.dto.AdminBookDetailsResponseDto;
import com.vttish.bookstore.books.dto.BookRequestDto;
import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.exception.BookHasOrdersException;
import com.vttish.bookstore.books.exception.BookNotFoundException;
import com.vttish.bookstore.books.mapper.BookMapper;
import com.vttish.bookstore.books.repository.BookRepository;
import com.vttish.bookstore.books.service.BookManagementService;
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
    private final BookMapper mapper;

    @Override
    @Transactional
    public AdminBookDetailsResponseDto create(BookRequestDto bookRequestDto) {
        Book book = mapper.toBook(bookRequestDto);
        return mapper.toAminBookDetailsDto(bookRepository.save(book));
    }

    @Override
    @Transactional
    public AdminBookDetailsResponseDto update(UUID id, BookRequestDto bookRequestDto) {
        Book book = getEntityById(id);

        mapper.update(bookRequestDto, book);
        return mapper.toAminBookDetailsDto(bookRepository.save(book));
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
