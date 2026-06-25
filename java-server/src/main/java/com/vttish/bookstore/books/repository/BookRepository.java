package com.vttish.bookstore.books.repository;

import com.vttish.bookstore.books.entity.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID>, JpaSpecificationExecutor<Book> {

    @EntityGraph(attributePaths = { "translations" })
    Optional<Book> findWithTranslationsById(UUID id);

    Optional<Book> findByIdAndIsArchivedFalse(UUID id);
}
