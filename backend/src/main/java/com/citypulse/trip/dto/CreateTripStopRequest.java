package com.citypulse.trip.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

public record CreateTripStopRequest(
    UUID placeId,

    @Size(max = 255, message = "Custom name cannot exceed 255 characters")
    String customName,

    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90.0")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90.0")
    Double latitude,

    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180.0")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180.0")
    Double longitude,

    @Min(value = 1, message = "Day number must be at least 1")
    Integer dayNumber,

    @Min(value = 0, message = "Order index must be at least 0")
    Integer orderIndex,

    LocalTime arrivalTime,
    LocalTime departureTime,

    @DecimalMin(value = "0.0", message = "Estimated cost must not be negative")
    BigDecimal estimatedCost,

    @Size(max = 1000, message = "Note cannot exceed 1000 characters")
    String note
) {}

