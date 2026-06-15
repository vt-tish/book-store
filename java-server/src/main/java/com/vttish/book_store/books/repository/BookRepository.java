package com.vttish.book_store.books.repository;

import com.vttish.book_store.books.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
}
