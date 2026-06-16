package com.vttish.bookstore.books.repository;

import com.vttish.bookstore.books.dto.BookPriceView;
import com.vttish.bookstore.books.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
    Page<Book> findAllByIsArchivedFalse(Pageable pageable);
    Optional<Book> findByIdAndIsArchivedFalse(UUID id);
    List<BookPriceView> findByIdInAndIsArchivedFalse(List<UUID> ids);

    @Query("SELECT b.price FROM Book b WHERE b.id = :id AND b.isArchived = false")
    Optional<BigDecimal> findPriceByIdAndIsArchivedFalse(@Param("id") UUID id);
}
