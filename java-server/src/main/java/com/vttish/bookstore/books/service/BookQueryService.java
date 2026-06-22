package com.vttish.bookstore.books.service;

import com.vttish.bookstore.books.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

public interface BookQueryService {
    Page<BookCardResponseDto> getAvailable(String lang, Pageable pageable);
    Page<AdminBookCardResponseDto> getAll(String lang, Pageable pageable);
    BookDetailsResponseDto getById(UUID id, String lang);
    AdminBookDetailsResponseDto getByIdAdmin(UUID id, String lang);

    Map<UUID, CartBookView> getBooksForCart(Set<UUID> ids);
    Map<UUID, OrderBookView> getBooksForOrder(Set<UUID> ids);
}
