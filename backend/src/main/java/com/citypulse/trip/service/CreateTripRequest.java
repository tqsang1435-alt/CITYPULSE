package com.citypulse.trip.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTripRequest(@NotBlank @Size(max = 200) String title) {}
