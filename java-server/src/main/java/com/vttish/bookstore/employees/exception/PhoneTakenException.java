package com.vttish.bookstore.employees.exception;

import com.vttish.bookstore.common.exception.ConflictException;

public class PhoneTakenException extends ConflictException {
    public PhoneTakenException() {
        super("error.employees.phone.taken");
    }
}
