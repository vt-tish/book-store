package com.vttish.bookstore.cart.exception;

import com.vttish.bookstore.common.exception.NotFoundException;

public class CartItemNotFoundException extends NotFoundException {
    public CartItemNotFoundException() {
        super("error.cart.cart_item.not_found");
    }
}
