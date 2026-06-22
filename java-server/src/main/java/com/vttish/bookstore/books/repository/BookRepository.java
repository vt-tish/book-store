package com.vttish.bookstore.books.repository;

import com.vttish.bookstore.books.dto.AdminBookCardResponseDto;
import com.vttish.bookstore.books.dto.AdminBookDetailsResponseDto;
import com.vttish.bookstore.books.dto.BookCardResponseDto;
import com.vttish.bookstore.books.dto.BookDetailsResponseDto;
import com.vttish.bookstore.books.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
    <T> Set<T> findByIdInAndIsArchivedFalse(Set<UUID> ids, Class<T> type);

    @Query("""
        SELECT new com.vttish.bookstore.books.dto.BookCardResponseDto(
            b.id,
            t.name,
            t.author,
            t.genre,
            b.price,
            b.previewUrl
        )
        FROM Book b
        JOIN b.translations t
        WHERE t.languageCode = :lang
        AND b.isArchived = false
    """)
    Page<BookCardResponseDto> findAllIsArchivedFalseCards(@Param("lang") String lang, Pageable pageable);

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
            CASE WHEN b.isArchived = true THEN false ELSE true END
        )
        FROM Book b
        JOIN b.translations t
        WHERE b.id = :id
        AND t.languageCode = :lang
    """)
    Optional<BookDetailsResponseDto> findByIdDetails(@Param("id") UUID id, @Param("lang") String lang);


    @Query("""
        SELECT new com.vttish.bookstore.books.dto.AdminBookCardResponseDto(
            b.id,
            t.name,
            t.author,
            t.genre,
            b.price,
            b.previewUrl,
            b.isArchived
        )
        FROM Book b
        JOIN b.translations t
        WHERE t.languageCode = :lang
    """)
    Page<AdminBookCardResponseDto> findAllAdminCards(@Param("lang") String lang, Pageable pageable);

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
