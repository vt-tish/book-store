package com.vttish.bookstore.books.repository;

import com.vttish.bookstore.books.dto.BookFilterRequestDto;
import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.entity.BookTranslation;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BookSpecifications {
    private BookSpecifications() {
        throw new UnsupportedOperationException();
    }

    public static Specification<Book> byFilter(BookFilterRequestDto filter) {
        Specification<Book> spec = Specification.unrestricted();

        if (filter == null) {
            return spec;
        }

        if (filter.search() != null && !filter.search().trim().isEmpty()) {
            spec = spec.and(search(filter.search()));
        }

        if (filter.minPrice() != null) {
            spec = spec.and(priceGreaterThanOrEqual(filter.minPrice()));
        }

        if (filter.maxPrice() != null) {
            spec = spec.and(priceLessThanOrEqual(filter.maxPrice()));
        }

        return spec;
    }

    public static Specification<Book> availableByFilter(BookFilterRequestDto filter) {
        return Specification.allOf(isAvailable(), byFilter(filter));
    }

    private static Specification<Book> priceGreaterThanOrEqual(BigDecimal minPrice) {
        return (root, query, cb) ->
            cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    private static Specification<Book> priceLessThanOrEqual(BigDecimal maxPrice) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    private static Specification<Book> isAvailable() {
        return (root, query, cb) -> cb.isFalse(root.get("isArchived"));
    }

    private static Specification<Book> search(String search) {
        return (root, query, cb) -> {
            String[] keywords = search.trim().toLowerCase().split("\\s+");
            List<Predicate> predicates = new ArrayList<>();

            for (String keyword : keywords) {
                String pattern = "%" + keyword + "%";

                Subquery<BookTranslation> subquery = query.subquery(BookTranslation.class);
                Root<BookTranslation> translation = subquery.from(BookTranslation.class);

                subquery.select(translation).where(
                        cb.and(
                                cb.equal(translation.get("book"), root),
                                cb.or(
                                        cb.like(cb.lower(translation.get("name")), pattern),
                                        cb.like(cb.lower(translation.get("author")), pattern),
                                        cb.like(cb.lower(translation.get("genre")), pattern)
                                )
                        )
                );

                predicates.add(cb.exists(subquery));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
