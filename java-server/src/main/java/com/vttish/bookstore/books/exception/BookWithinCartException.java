package com.vttish.bookstore.books.exception;

import com.vttish.bookstore.common.exception.ConflictException;

public class BookWithinCartException extends ConflictException {
    public BookWithinCartException() {
        super("error.books.book.within_cart");
    }
}
