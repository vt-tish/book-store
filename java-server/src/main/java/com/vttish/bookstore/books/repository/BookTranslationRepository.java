package com.vttish.bookstore.books.repository;

import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.entity.BookTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookTranslationRepository extends JpaRepository<BookTranslation, UUID> {
    List<BookTranslation> findByBookInAndLanguageCodeIn(List<Book> bookIds, List<String> languageCode);
}
