package com.vttish.bookstore.auth.exception;

import com.vttish.bookstore.common.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
        super("error.auth.user.not_found");
    }
}
