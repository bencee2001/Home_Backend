package com.home.db;

import java.sql.SQLException;

public final class SqlErrors {

    // PostgreSQL SQLSTATE codes
    public static final String FOREIGN_KEY_VIOLATION = "23503";
    public static final String UNIQUE_VIOLATION = "23505";

    private SqlErrors() {
    }

    // Walks the cause chain, since the driver error may be wrapped
    public static boolean hasState(Throwable e, String sqlState) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof SQLException sql && sqlState.equals(sql.getSQLState())) {
                return true;
            }
        }
        return false;
    }
}
