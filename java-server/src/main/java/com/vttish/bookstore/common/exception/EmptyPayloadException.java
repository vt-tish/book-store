package com.vttish.bookstore.common.exception;

public class EmptyPayloadException extends RuntimeException {
    public EmptyPayloadException(String message) {
        super(message);
    }
}
