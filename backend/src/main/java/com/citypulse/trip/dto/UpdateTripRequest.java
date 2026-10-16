package com.citypulse.trip.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateTripRequest(
    @NotBlank(message = "Trip title is required")
    @Size(max = 150, message = "Title cannot exceed 150 characters")
    String title,

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    String description,

    @NotNull(message = "Start date is required")
    LocalDate startDate,

    @NotNull(message = "End date is required")
    LocalDate endDate,

    @DecimalMin(value = "0.0", message = "Budget must not be negative")
    BigDecimal budget
) {}

