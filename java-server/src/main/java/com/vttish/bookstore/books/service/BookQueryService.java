package com.vttish.bookstore.books.service;

import com.vttish.bookstore.books.dto.*;
import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.entity.BookTranslation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

public interface BookQueryService {
    Page<BookCardResponseDto> getAvailable(String search, String lang, Pageable pageable);
    Page<AdminBookCardResponseDto> getAll(String search, String lang, Pageable pageable);
    BookDetailsResponseDto getById(UUID id, String lang);
    AdminBookDetailsResponseDto getByIdAdmin(UUID id, String lang);

    Book getByIdAvailable(UUID id);
    Map<UUID, BookTranslation> getTranslations(List<Book> books, String lang);
}
