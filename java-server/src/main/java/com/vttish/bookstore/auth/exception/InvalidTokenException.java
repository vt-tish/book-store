package com.vttish.bookstore.auth.exception;

import com.vttish.bookstore.common.exception.UnauthorizedException;

public class InvalidTokenException extends UnauthorizedException {
    public InvalidTokenException() {
        super("error.auth.token.invalid");
    }
}
