package com.vttish.bookstore.common.exception;

public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException(Class<?> entityClass, Object id) {
        super(String.format("%s with id %s is not found", entityClass.getSimpleName(), id));
    }
    public EntityNotFoundException(String message) {
        super(message);
    }
}
