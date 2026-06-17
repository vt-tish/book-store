package com.vttish.bookstore.books.repository;

import com.vttish.bookstore.books.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
    Page<Book> findAllByIsArchivedFalse(Pageable pageable);

    <T> Set<T> findByIdInAndIsArchivedFalse(Set<UUID> ids, Class<T> type);
}
