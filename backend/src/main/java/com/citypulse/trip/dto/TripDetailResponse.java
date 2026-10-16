package com.citypulse.trip.dto;

import com.citypulse.trip.entity.Trip;
import com.citypulse.trip.entity.TripStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record TripDetailResponse(
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
    BigDecimal totalEstimatedCost,
    List<TripStopResponse> stops,
    Instant createdAt,
    Instant updatedAt
) {
    public static TripDetailResponse fromEntity(Trip trip) {
        if (trip == null) {
            return null;
        }

        List<TripStopResponse> stopResponses = trip.getStops() != null
            ? trip.getStops().stream()
                .sorted((a, b) -> {
                    int c = Integer.compare(
                        a.getDayNumber() != null ? a.getDayNumber() : 0,
                        b.getDayNumber() != null ? b.getDayNumber() : 0
                    );
                    if (c != 0) return c;
                    return Integer.compare(
                        a.getOrderIndex() != null ? a.getOrderIndex() : 0,
                        b.getOrderIndex() != null ? b.getOrderIndex() : 0
                    );
                })
                .map(TripStopResponse::fromEntity)
                .toList()
            : List.of();

        BigDecimal totalCost = BigDecimal.ZERO;
        if (trip.getStops() != null) {
            for (var stop : trip.getStops()) {
                if (stop.getEstimatedCost() != null) {
                    totalCost = totalCost.add(stop.getEstimatedCost());
                }
            }
        }

        return new TripDetailResponse(
            trip.getId(),
            trip.getOwnerId(),
            trip.getTitle(),
            trip.getDescription(),
            trip.getStartDate(),
            trip.getEndDate(),
            trip.getBudget(),
            trip.getStatus(),
            trip.getTotalDays(),
            stopResponses.size(),
            totalCost,
            stopResponses,
            trip.getCreatedAt(),
            trip.getUpdatedAt()
        );
    }
}

