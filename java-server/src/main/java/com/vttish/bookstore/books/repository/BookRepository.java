package com.vttish.bookstore.books.repository;

import com.vttish.bookstore.books.dto.AdminBookCardResponseDto;
import com.vttish.bookstore.books.dto.AdminBookDetailsResponseDto;
import com.vttish.bookstore.books.dto.BookCardResponseDto;
import com.vttish.bookstore.books.dto.BookDetailsResponseDto;
import com.vttish.bookstore.books.entity.Book;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID>, JpaSpecificationExecutor<Book> {

    Optional<Book> findByIdAndIsArchivedFalse(UUID id);

    @EntityGraph(attributePaths = "translations")
    @NonNull Page<Book> findAll(Specification<Book> specification, @NonNull Pageable pageable);

    @Query("""
        SELECT new com.vttish.bookstore.books.dto.BookDetailsResponseDto(
            b.id,
            t.name,
            t.genre,
            b.previewUrl,
            b.ageGroup,
            b.price,
            b.publicationDate,
            t.author,
            b.pages,
            t.characteristics,
            t.description,
            b.language,
            (b.isArchived = false)
        )
        FROM Book b
        JOIN b.translations t
        WHERE b.id = :id
        AND t.languageCode = :lang
    """)
    Optional<BookDetailsResponseDto> findByIdDetails(@Param("id") UUID id, @Param("lang") String lang);

    @Query("""
        SELECT new com.vttish.bookstore.books.dto.AdminBookDetailsResponseDto(
            b.id,
            t.name,
            t.genre,
            b.previewUrl,
            b.ageGroup,
            b.price,
            b.publicationDate,
            t.author,
            b.pages,
            t.characteristics,
            t.description,
            b.language,
            b.createdAt,
            b.updatedAt,
            b.isArchived
        )
        FROM Book b
        JOIN b.translations t
        WHERE b.id = :id
        AND t.languageCode = :lang
    """)
    Optional<AdminBookDetailsResponseDto> findByIdAdminDetails(@Param("id") UUID id, @Param("lang") String lang);
}
