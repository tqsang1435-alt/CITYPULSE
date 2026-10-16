package com.citypulse.trip.service;

import com.citypulse.trip.dto.CreateTripRequest;
import com.citypulse.trip.dto.CreateTripStopRequest;
import com.citypulse.trip.dto.ReorderStopsRequest;
import com.citypulse.trip.dto.TripDetailResponse;
import com.citypulse.trip.dto.TripResponse;
import com.citypulse.trip.dto.TripStopResponse;
import com.citypulse.trip.dto.UpdateTripRequest;
import com.citypulse.trip.dto.UpdateTripStopRequest;
import com.citypulse.trip.entity.TripStatus;

import java.util.List;
import java.util.UUID;

public interface TripService {

    TripDetailResponse createTrip(CreateTripRequest request, UUID ownerId);

    TripDetailResponse getTripById(UUID tripId, UUID requesterId);

    List<TripResponse> listTripsByOwner(UUID ownerId, TripStatus status);

    TripDetailResponse updateTrip(UUID tripId, UpdateTripRequest request, UUID requesterId);

    TripResponse archiveTrip(UUID tripId, UUID requesterId);

    TripResponse restoreTrip(UUID tripId, UUID requesterId);

    void deleteTrip(UUID tripId, UUID requesterId);

    TripStopResponse addStop(UUID tripId, CreateTripStopRequest request, UUID requesterId);

    TripStopResponse updateStop(UUID tripId, UUID stopId, UpdateTripStopRequest request, UUID requesterId);

    void deleteStop(UUID tripId, UUID stopId, UUID requesterId);

    TripDetailResponse reorderStops(UUID tripId, ReorderStopsRequest request, UUID requesterId);
}

