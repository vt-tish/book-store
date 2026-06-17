package com.vttish.bookstore.books.service.impl;

import com.vttish.bookstore.books.dto.AdminBookDetailsDto;
import com.vttish.bookstore.books.dto.BookDto;
import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.mapper.BookMapper;
import com.vttish.bookstore.books.repository.BookRepository;
import com.vttish.bookstore.books.service.BookManagementService;
import com.vttish.bookstore.common.exception.BadRequestException;
import com.vttish.bookstore.common.exception.EntityNotFoundException;
import com.vttish.bookstore.orders.service.OrderQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class BookManagementServiceImpl implements BookManagementService {
    private final BookRepository bookRepository;
    private final OrderQueryService orderQueryService;
    private final BookMapper mapper;

    @Override
    public AdminBookDetailsDto create(BookDto bookDto) {
        Book book = mapper.toBook(bookDto);
        return mapper.toAminBookDetailsDto(bookRepository.save(book));
    }

    @Override
    public AdminBookDetailsDto update(UUID id, BookDto bookDto) {
        Book book = getEntityById(id);

        mapper.update(bookDto, book);
        return mapper.toAminBookDetailsDto(bookRepository.save(book));
    }

    @Override
    public void delete(UUID id) {
        if (orderQueryService.hasBookBeenOrdered(id)) {
            throw new BadRequestException(
                    String.format("Book with id %s is associated with orders, archive book instead", id)
            );
        }

        bookRepository.deleteById(id);
    }

    @Override
    public void archive(UUID id) {
        Book book = getEntityById(id);

        if (book.isArchived()) {
            return;
        }

        book.setArchived(true);
        bookRepository.save(book);
    }

    @Override
    public void unarchive(UUID id) {
        Book book = getEntityById(id);

        if (!book.isArchived()) {
            return;
        }

        book.setArchived(false);
        bookRepository.save(book);
    }

    private Book getEntityById(UUID id) {
        return bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(Book.class, id)
        );
    }
}
