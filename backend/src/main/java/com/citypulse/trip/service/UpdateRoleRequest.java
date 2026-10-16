package com.citypulse.trip.service;

import com.citypulse.trip.entity.TripRole;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleRequest(
        @NotNull(message = "Role không được để trống")
        TripRole role
) {}

