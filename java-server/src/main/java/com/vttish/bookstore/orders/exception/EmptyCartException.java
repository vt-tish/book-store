package com.vttish.bookstore.orders.exception;

import com.vttish.bookstore.common.exception.ConflictException;

public class EmptyCartException extends ConflictException {
    public EmptyCartException() {
        super("error.orders.cart.empty");
    }
}
