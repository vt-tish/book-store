package com.vttish.bookstore.books.service.impl;

import com.vttish.bookstore.books.dto.AdminBookDetailsDto;
import com.vttish.bookstore.books.dto.BookDto;
import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.mapper.BookMapper;
import com.vttish.bookstore.books.repository.BookRepository;
import com.vttish.bookstore.books.service.BookManagementService;
import com.vttish.bookstore.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class BookManagementServiceImpl implements BookManagementService {
    private BookRepository bookRepository;
    private BookMapper mapper;

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
        Book book = getEntityById(id);

        if (true) {
            book.setArchived(true);
            bookRepository.save(book);
        } else {
            bookRepository.deleteById(id);
        }
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
                () -> new NotFoundException(Book.class, id)
        );
    }
}
