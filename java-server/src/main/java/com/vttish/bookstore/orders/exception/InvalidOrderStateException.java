package com.vttish.bookstore.orders.exception;

import com.vttish.bookstore.common.exception.ConflictException;

public class InvalidOrderStateException extends ConflictException {
    public InvalidOrderStateException(String currentStatus) {
        super("error.orders.order.invalid_state", new Object[]{ currentStatus });
    }
}
