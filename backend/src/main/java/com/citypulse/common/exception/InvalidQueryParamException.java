package com.citypulse.common.exception;

/**
 * Thrown when an invalid query, filter, pagination or sort parameter is supplied by client.
 */
public class InvalidQueryParamException extends RuntimeException {
    public InvalidQueryParamException(String message) {
        super(message);
    }
}
