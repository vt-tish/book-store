package com.vttish.bookstore.common.exception;

import org.springframework.http.HttpStatus;

public abstract class ConflictException extends BaseException {
    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public ConflictException(String message, Object[] args) {
        super(message, HttpStatus.CONFLICT, args);
    }
}
