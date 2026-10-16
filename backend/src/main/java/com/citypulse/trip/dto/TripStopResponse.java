package com.citypulse.trip.dto;

import com.citypulse.trip.entity.TripStop;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

public record TripStopResponse(
    UUID id,
    UUID tripId,
    UUID placeId,
    String customName,
    Double latitude,
    Double longitude,
    Integer dayNumber,
    Integer orderIndex,
    LocalTime arrivalTime,
    LocalTime departureTime,
    BigDecimal estimatedCost,
    String note,
    Instant createdAt,
    Instant updatedAt
) {
    public static TripStopResponse fromEntity(TripStop stop) {
        if (stop == null) {
            return null;
        }
        return new TripStopResponse(
            stop.getId(),
            stop.getTrip() != null ? stop.getTrip().getId() : null,
            stop.getPlaceId(),
            stop.getCustomName(),
            stop.getLatitude(),
            stop.getLongitude(),
            stop.getDayNumber(),
            stop.getOrderIndex(),
            stop.getArrivalTime(),
            stop.getDepartureTime(),
            stop.getEstimatedCost(),
            stop.getNote(),
            stop.getCreatedAt(),
            stop.getUpdatedAt()
        );
    }
}

