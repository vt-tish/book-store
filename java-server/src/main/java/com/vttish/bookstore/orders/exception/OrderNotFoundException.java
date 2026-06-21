package com.vttish.bookstore.orders.exception;

import com.vttish.bookstore.common.exception.NotFoundException;

public class OrderNotFoundException extends NotFoundException {
    public OrderNotFoundException() {
        super("error.orders.order.not_found");
    }
}
