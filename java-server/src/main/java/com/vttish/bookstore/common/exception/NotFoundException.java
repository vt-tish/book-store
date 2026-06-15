package com.vttish.bookstore.common.exception;

public class NotFoundException extends RuntimeException{
    public NotFoundException(Class<?> entityClass, Object id) {
        super(String.format("%s with id %s is not found", entityClass.getSimpleName(), id));
    }
    public NotFoundException(String message) {
        super(message);
    }
}
