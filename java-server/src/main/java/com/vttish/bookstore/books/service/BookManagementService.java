package com.vttish.bookstore.books.service;

import com.vttish.bookstore.books.dto.AdminBookDetailsDto;
import com.vttish.bookstore.books.dto.BookDto;

import java.util.UUID;

public interface BookManagementService {
    AdminBookDetailsDto create(BookDto bookDto);
    AdminBookDetailsDto update(UUID id, BookDto bookDto);
    void delete(UUID id);
    void archive(UUID id);
    void unarchive(UUID id);
}
