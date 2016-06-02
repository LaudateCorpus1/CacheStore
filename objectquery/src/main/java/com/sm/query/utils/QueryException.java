package com.sm.query.utils;

/**
 * Created by mhsieh on 12/31/14.
 */
public class QueryException extends RuntimeException {
    public QueryException(String message ) {
        super(message);
    }

    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
