package com.vttish.bookstore.common.exception;

import org.springframework.http.HttpStatus;

public abstract class TooManyRequestsException extends BaseException {
    public TooManyRequestsException(String message, Object[] args) {
        super(message, HttpStatus.TOO_MANY_REQUESTS, args);
    }
}
