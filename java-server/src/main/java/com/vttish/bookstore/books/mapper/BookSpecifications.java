package com.vttish.bookstore.books.mapper;

import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.entity.BookTranslation;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
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
            Join<Book, BookTranslation> translations = root.join("translations", JoinType.LEFT);
            q.distinct(true);

            List<Predicate> predicates = new ArrayList<>();
            for (String keyword : keywords) {
                String pattern = "%" + keyword+ "%";

                predicates.add(cb.or(
                        cb.like(cb.lower(translations.get("name")), pattern),
                        cb.like(cb.lower(translations.get("author")), pattern)
                ));
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
