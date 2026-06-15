package com.vttish.book_store.books.service.impl;

import com.vttish.book_store.books.dto.BookCardDto;
import com.vttish.book_store.books.dto.BookDetailsDto;
import com.vttish.book_store.books.dto.BookDto;
import com.vttish.book_store.books.entity.Book;
import com.vttish.book_store.books.mapper.BookMapper;
import com.vttish.book_store.books.repository.BookRepository;
import com.vttish.book_store.books.service.BookService;
import com.vttish.book_store.common.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper mapper;

    public BookServiceImpl(BookRepository bookRepository, BookMapper mapper) {
        this.bookRepository = bookRepository;
        this.mapper = mapper;
    }

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
        return bookRepository.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("Book with id %s is not found", id)
        ));
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
            throw new NotFoundException(String.format("Book with id %s is not found", id));
        }

        bookRepository.deleteById(id);
    }
}
