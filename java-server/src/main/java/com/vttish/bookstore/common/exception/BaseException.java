package com.vttish.bookstore.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {
    private final HttpStatus status;
    private final Object[] args;

    public BaseException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.args = null;
    }

    public BaseException(String message, HttpStatus status, Object[] args) {
        super(message);
        this.status = status;
        this.args = args;
    }
}
