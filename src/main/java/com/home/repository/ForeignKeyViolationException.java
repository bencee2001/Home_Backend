package com.home.repository;

public class ForeignKeyViolationException extends DataAccessException {

    public ForeignKeyViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
