package com.vttish.bookstore.auth.exception;

public class EmailDeliveryException extends RuntimeException {
    public EmailDeliveryException(String message, Exception ex) {
        super(message, ex);
    }
}
