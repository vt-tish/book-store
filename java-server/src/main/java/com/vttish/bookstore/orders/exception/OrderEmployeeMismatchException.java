package com.vttish.bookstore.orders.exception;

import com.vttish.bookstore.common.exception.ForbiddenException;

public class OrderEmployeeMismatchException extends ForbiddenException {
    public OrderEmployeeMismatchException() {
        super("error.orders.order.employee_mismatch");
    }
}
