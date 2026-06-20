package com.vttish.bookstore.books.service;

import com.vttish.bookstore.books.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

public interface BookQueryService {
    Page<BookCardResponseDto> getAvailable(Pageable pageable);
    Page<AdminBookCardResponseDto> getAll(Pageable pageable);
    BookDetailsResponseDto getById(UUID id);
    AdminBookDetailsResponseDto getByIdAdmin(UUID id);

    Map<UUID, CartBookView> getBooksForCart(Set<UUID> ids);
    Map<UUID, OrderBookView> getBooksForOrder(Set<UUID> ids);
}
