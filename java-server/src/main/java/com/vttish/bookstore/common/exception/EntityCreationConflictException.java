package com.vttish.bookstore.common.exception;

public class EntityCreationConflictException extends RuntimeException {
    public EntityCreationConflictException(String message, Throwable cause) {
        super(message, cause);
    }
    public EntityCreationConflictException(String message) {
        super(message);
    }
}
