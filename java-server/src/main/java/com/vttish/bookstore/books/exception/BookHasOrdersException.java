package com.vttish.bookstore.books.exception;

import com.vttish.bookstore.common.exception.ConflictException;

public class BookHasOrdersException extends ConflictException {
    public BookHasOrdersException() {
        super("error.books.book.has_orders");
    }
}
