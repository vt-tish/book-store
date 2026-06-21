package com.vttish.bookstore.orders.exception;

import com.vttish.bookstore.common.exception.ConflictException;

public class UnavailableBookException extends ConflictException {
    public UnavailableBookException() {
        super("error.orders.book.unavailable");
    }
}
