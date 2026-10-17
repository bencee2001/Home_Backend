package com.home.repository;

import java.sql.SQLException;

final class SqlErrors {

    // PostgreSQL SQLSTATE codes
    private static final String FOREIGN_KEY_VIOLATION = "23503";
    private static final String UNIQUE_VIOLATION = "23505";

    private SqlErrors() {
    }

    // Maps a driver error (possibly wrapped by Wood) onto our exception hierarchy
    static DataAccessException translate(String operation, Exception e) {
        String state = sqlState(e);
        if (FOREIGN_KEY_VIOLATION.equals(state)) {
            return new ForeignKeyViolationException(operation + ": foreign key violation", e);
        }
        if (UNIQUE_VIOLATION.equals(state)) {
            return new DuplicateKeyException(operation + ": duplicate key", e);
        }
        return new DataAccessException(operation + " failed", e);
    }

    private static String sqlState(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof SQLException sql && sql.getSQLState() != null) {
                return sql.getSQLState();
            }
        }
        return null;
    }
}
