package com.home.repository;

public class DuplicateKeyException extends DataAccessException {

    public DuplicateKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
