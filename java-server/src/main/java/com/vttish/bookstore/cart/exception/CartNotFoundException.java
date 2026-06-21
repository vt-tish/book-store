package com.vttish.bookstore.cart.exception;

import com.vttish.bookstore.common.exception.NotFoundException;

public class CartNotFoundException extends NotFoundException {
    public CartNotFoundException() {
        super("error.cart.cart.not_found");
    }
}
