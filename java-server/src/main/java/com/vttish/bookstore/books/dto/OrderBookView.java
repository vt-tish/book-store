package com.vttish.bookstore.books.dto;

import java.util.UUID;

public interface OrderBookView {
    UUID getId();
    String getName();
    String getAuthor();
}
