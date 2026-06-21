package com.vttish.bookstore.auth.exception;

import com.vttish.bookstore.common.exception.ForbiddenException;

public class BlockedUserException extends ForbiddenException {
    public BlockedUserException() {
        super("error.auth.user.blocked");
    }
}
