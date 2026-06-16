package com.vttish.bookstore.books.service.impl;

import com.vttish.bookstore.books.dto.BookPriceView;
import com.vttish.bookstore.books.repository.BookRepository;
import com.vttish.bookstore.books.service.BookPriceProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookPriceProviderImpl implements BookPriceProvider {
    private final BookRepository bookRepository;

    @Override
    public Optional<BigDecimal> getPriceById(UUID id) {
        return bookRepository.findPriceByIdAndIsArchivedFalse(id);
    }

    @Override
    public Map<UUID, BigDecimal> getPricesByIds(List<UUID> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) {
            return Map.of();
        }

        List<BookPriceView> prices = bookRepository.findByIdInAndIsArchivedFalse(bookIds);

        return prices.stream()
                .collect(Collectors.toMap(BookPriceView::getId, BookPriceView::getPrice));
    }
}
