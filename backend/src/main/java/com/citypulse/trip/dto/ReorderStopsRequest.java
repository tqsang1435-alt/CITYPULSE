package com.citypulse.trip.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ReorderStopsRequest(
    @NotEmpty(message = "Stops list to reorder cannot be empty")
    List<@Valid StopOrderItem> items
) {
    public record StopOrderItem(
        @NotNull(message = "Stop id is required")
        UUID stopId,

        @NotNull(message = "Day number is required")
        Integer dayNumber,

        @NotNull(message = "Order index is required")
        Integer orderIndex
    ) {}
}

