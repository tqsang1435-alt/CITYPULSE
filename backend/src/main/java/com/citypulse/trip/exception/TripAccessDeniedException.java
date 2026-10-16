package com.citypulse.trip.exception;

import java.util.UUID;

public class TripAccessDeniedException extends RuntimeException {
    public TripAccessDeniedException(String message) {
        super(message);
    }

    public TripAccessDeniedException(UUID tripId, UUID userId) {
        super("User " + userId + " does not have permission to access or modify trip " + tripId);
    }
}

