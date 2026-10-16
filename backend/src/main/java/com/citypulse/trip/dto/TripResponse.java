package com.citypulse.trip.dto;

import com.citypulse.trip.entity.Trip;
import com.citypulse.trip.entity.TripStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TripResponse(
    UUID id,
    UUID ownerId,
    String title,
    String description,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal budget,
    TripStatus status,
    long totalDays,
    int stopCount,
    Instant createdAt,
    Instant updatedAt
) {
    public static TripResponse fromEntity(Trip trip) {
        if (trip == null) {
            return null;
        }
        return new TripResponse(
            trip.getId(),
            trip.getOwnerId(),
            trip.getTitle(),
            trip.getDescription(),
            trip.getStartDate(),
            trip.getEndDate(),
            trip.getBudget(),
            trip.getStatus(),
            trip.getTotalDays(),
            trip.getStops() != null ? trip.getStops().size() : 0,
            trip.getCreatedAt(),
            trip.getUpdatedAt()
        );
    }
}

