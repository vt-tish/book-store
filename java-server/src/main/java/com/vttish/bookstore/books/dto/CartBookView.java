package com.vttish.bookstore.books.dto;

import java.math.BigDecimal;
import java.util.UUID;

public interface CartBookView {
    UUID getId();
    String getName();
    BigDecimal getPrice();
}
