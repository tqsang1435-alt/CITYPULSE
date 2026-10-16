package com.citypulse.trip.service;

import com.citypulse.trip.entity.Trip;

import java.time.Instant;

public record TripResponse(Long id, String title, Instant createdAt) {
    public static TripResponse from(Trip trip) {
        return new TripResponse(trip.getId(), trip.getTitle(), trip.getCreatedAt());
    }
}
