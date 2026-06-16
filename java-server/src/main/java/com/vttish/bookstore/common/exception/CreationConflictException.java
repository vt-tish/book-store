package com.vttish.bookstore.common.exception;

public class CreationConflictException extends RuntimeException {
    public CreationConflictException(String message, Throwable cause) {
        super(message, cause);
    }
    public CreationConflictException(String message) {
        super(message);
    }
}
