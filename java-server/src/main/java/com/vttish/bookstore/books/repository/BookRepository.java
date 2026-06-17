package com.vttish.bookstore.books.repository;

import com.vttish.bookstore.books.dto.BookPriceView;
import com.vttish.bookstore.books.dto.CartBookView;
import com.vttish.bookstore.books.dto.OrderBookView;
import com.vttish.bookstore.books.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
    Page<Book> findAllByIsArchivedFalse(Pageable pageable);
    Optional<CartBookView> findByIdAndIsArchivedFalse(UUID id);

    <T> Set<T> findByIdInAndIsArchivedFalse(Set<UUID> ids, Class<T> type);
}
