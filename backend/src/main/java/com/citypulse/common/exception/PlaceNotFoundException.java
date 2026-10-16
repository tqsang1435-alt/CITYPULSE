package com.citypulse.common.exception;

/**
 * Thrown when a requested place is not found or is not published.
 */
public class PlaceNotFoundException extends RuntimeException {
    public PlaceNotFoundException(String message) {
        super(message);
    }
}
