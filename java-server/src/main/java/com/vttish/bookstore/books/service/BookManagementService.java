package com.vttish.bookstore.books.service;

import com.vttish.bookstore.books.dto.AdminBookDetailsResponseDto;
import com.vttish.bookstore.books.dto.BookRequestDto;

import java.util.UUID;

public interface BookManagementService {
    AdminBookDetailsResponseDto create(String lang, BookRequestDto bookRequestDto);
    AdminBookDetailsResponseDto update(UUID id, String lang, BookRequestDto bookRequestDto);
    void delete(UUID id);
    void archive(UUID id);
    void unarchive(UUID id);
}
