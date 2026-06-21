package com.vttish.bookstore.auth.exception;

import com.vttish.bookstore.common.exception.UnauthorizedException;

public class UnverifiedUserException extends UnauthorizedException {
    public UnverifiedUserException() {
        super("error.auth.user.unverified");
    }
}
