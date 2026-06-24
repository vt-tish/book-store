package com.vttish.bookstore.books.repository;

import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.entity.BookTranslation;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BookSpecifications {
    private BookSpecifications() {
        throw new UnsupportedOperationException();
    }

    public static Specification<Book> search(String query) {
        return (root, q, cb) -> {
            if (query == null || query.trim().isEmpty()) {
                return cb.conjunction();
            }

            String[] keywords = query.trim().toLowerCase().split("\\s+");
            List<Predicate> predicates = new ArrayList<>();

            for (String keyword : keywords) {
                String pattern = "%" + keyword + "%";

                Subquery<BookTranslation> subquery = q.subquery(BookTranslation.class);
                Root<BookTranslation> translation = subquery.from(BookTranslation.class);

                subquery.select(translation).where(
                        cb.and(
                                cb.equal(translation.get("book"), root),
                                cb.or(
                                        cb.like(cb.lower(translation.get("name")), pattern),
                                        cb.like(cb.lower(translation.get("author")), pattern)
                                )
                        )
                );

                predicates.add(cb.exists(subquery));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    public static Specification<Book> searchAvailable(String query) {
        return Specification.allOf(isAvailable(), search(query));
    }

    private static Specification<Book> isAvailable() {
        return (root, query, cb) -> {
            return cb.isFalse(root.get("isArchived"));
        };
    }
}
