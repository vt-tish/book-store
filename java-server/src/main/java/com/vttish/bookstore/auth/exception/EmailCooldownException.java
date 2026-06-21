package com.vttish.bookstore.auth.exception;

import com.vttish.bookstore.common.exception.TooManyRequestsException;

public class EmailCooldownException extends TooManyRequestsException {
    public EmailCooldownException(Long secondsRemaining) {
        super("error.auth.email_cooldown", new Object[]{ secondsRemaining });
    }
}
