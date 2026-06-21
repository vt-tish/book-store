package com.vttish.bookstore.auth.exception;

import com.vttish.bookstore.common.exception.UnauthorizedException;

public class RevokedTokenException extends UnauthorizedException {
    public RevokedTokenException() {
        super("error.auth.token.revoked");
    }
}
