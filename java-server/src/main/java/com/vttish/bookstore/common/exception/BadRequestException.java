package com.vttish.bookstore.common.exception;

import org.springframework.http.HttpStatus;

public abstract class BadRequestException extends BaseException {
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
