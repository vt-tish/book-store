package com.vttish.bookstore.books.exception;

import com.vttish.bookstore.common.exception.NotFoundException;

public class BookNotFoundException extends NotFoundException {
    public BookNotFoundException() {
        super("error.books.book.not_found");
    }
}
