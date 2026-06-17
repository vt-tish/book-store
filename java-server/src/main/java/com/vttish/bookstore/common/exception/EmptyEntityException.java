package com.vttish.bookstore.common.exception;

public class EmptyEntityException extends RuntimeException {
    public EmptyEntityException(String message) {
        super(message);
    }
}
