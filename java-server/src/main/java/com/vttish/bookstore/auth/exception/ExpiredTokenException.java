package com.vttish.bookstore.auth.exception;

import com.vttish.bookstore.common.exception.UnauthorizedException;

public class ExpiredTokenException extends UnauthorizedException {
    public ExpiredTokenException() {
        super("error.auth.token.expired");
    }
}
