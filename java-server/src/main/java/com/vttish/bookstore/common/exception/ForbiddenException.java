package com.vttish.bookstore.common.exception;

import org.springframework.http.HttpStatus;

public abstract class ForbiddenException extends BaseException {
    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
