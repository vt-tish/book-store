package com.vttish.bookstore.cart.service;

import java.util.UUID;

public interface CartQueryService {
    boolean containsBook(UUID bookId);
}
