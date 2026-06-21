package com.vttish.bookstore.common.exception;

import org.springframework.http.HttpStatus;

public abstract class UnauthorizedException extends BaseException {
    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
