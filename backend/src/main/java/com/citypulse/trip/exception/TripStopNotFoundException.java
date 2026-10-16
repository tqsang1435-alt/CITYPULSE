package com.citypulse.trip.exception;

import java.util.UUID;

public class TripStopNotFoundException extends RuntimeException {
    public TripStopNotFoundException(UUID stopId) {
        super("Trip stop not found with id: " + stopId);
    }

    public TripStopNotFoundException(String message) {
        super(message);
    }
}

