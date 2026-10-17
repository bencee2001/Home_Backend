package com.home.repository;

// Unchecked wrapper for database failures, so callers don't deal with SQLException
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
