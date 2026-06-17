package com.vttish.bookstore.books.service;

import com.vttish.bookstore.books.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.*;

public interface BookService {
    Page<BookCardDto> getAvailable(Pageable pageable);
    Page<AdminBookCardDto> getAll(Pageable pageable);
    BookDetailsDto getById(UUID id);
    AdminBookDetailsDto getByIdAdmin(UUID id);

    CartBookView getAvailableByIdView(UUID id);
    Map<UUID, BigDecimal> getPricesByIds(Set<UUID> ids);
}
