package com.vttish.bookstore.auth.exception;

import com.vttish.bookstore.common.exception.UnauthorizedException;

public class TokenReuseException extends UnauthorizedException {
    public TokenReuseException() {
        super("error.auth.token.reuse");
    }
}
