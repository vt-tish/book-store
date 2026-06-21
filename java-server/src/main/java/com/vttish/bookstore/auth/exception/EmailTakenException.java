package com.vttish.bookstore.auth.exception;

import com.vttish.bookstore.common.exception.ConflictException;

public class EmailTakenException extends ConflictException {
    public EmailTakenException() {
        super("error.auth.email.taken");
    }
}
