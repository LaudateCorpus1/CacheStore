package com.sm.query.utils;

/**
 * Created by mhsieh on 12/30/14.
 */
public class ObjectIdException extends RuntimeException {

    public ObjectIdException(String message) {
        super(message);
    }

    public ObjectIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
