package com.vttish.bookstore.books.repository;

import com.vttish.bookstore.books.dto.BookPriceView;
import com.vttish.bookstore.books.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
    List<BookPriceView> findByIdIn(List<UUID> ids);
}
