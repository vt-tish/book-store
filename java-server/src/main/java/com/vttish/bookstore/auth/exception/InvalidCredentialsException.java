package com.vttish.bookstore.auth.exception;

import com.vttish.bookstore.common.exception.UnauthorizedException;

public class InvalidCredentialsException extends UnauthorizedException {
    public InvalidCredentialsException() {
        super("error.auth.credentials.invalid");
    }
}
