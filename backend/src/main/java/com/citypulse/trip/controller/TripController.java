package com.citypulse.trip.controller;

import com.citypulse.common.security.CurrentUserProvider;
import com.citypulse.trip.dto.CreateTripRequest;
import com.citypulse.trip.dto.CreateTripStopRequest;
import com.citypulse.trip.dto.ReorderStopsRequest;
import com.citypulse.trip.dto.TripDetailResponse;
import com.citypulse.trip.dto.TripResponse;
import com.citypulse.trip.dto.TripStopResponse;
import com.citypulse.trip.dto.UpdateTripRequest;
import com.citypulse.trip.dto.UpdateTripStopRequest;
import com.citypulse.trip.entity.TripStatus;
import com.citypulse.trip.service.TripService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for Trip and Stop management.
 *
 * NOTE ON TV1 DECOUPLING:
 * The current user ID is resolved via the CurrentUserProvider interface.
 * During this independent stage, an interim provider is used.
 * When TV1 completes Auth, their SecurityContext/JWT provider will automatically supply the ID.
 */
@RestController
@RequestMapping("/api/v1/trips")
public class TripController {

    private final TripService tripService;
    private final CurrentUserProvider currentUserProvider;

    public TripController(TripService tripService, CurrentUserProvider currentUserProvider) {
        this.tripService = tripService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TripDetailResponse createTrip(@Valid @RequestBody CreateTripRequest request) {
        UUID currentUserId = currentUserProvider.requireCurrentUserId();
        return tripService.createTrip(request, currentUserId);
    }

    @GetMapping
    public List<TripResponse> listTrips(@RequestParam(required = false) TripStatus status) {
        UUID currentUserId = currentUserProvider.requireCurrentUserId();
        return tripService.listTripsByOwner(currentUserId, status);
    }

    @GetMapping("/{tripId}")
    public TripDetailResponse getTrip(@PathVariable UUID tripId) {
        UUID currentUserId = currentUserProvider.requireCurrentUserId();
        return tripService.getTripById(tripId, currentUserId);
    }

    @PutMapping("/{tripId}")
    public TripDetailResponse updateTrip(
        @PathVariable UUID tripId,
        @Valid @RequestBody UpdateTripRequest request
    ) {
        UUID currentUserId = currentUserProvider.requireCurrentUserId();
        return tripService.updateTrip(tripId, request, currentUserId);
    }

    @PatchMapping("/{tripId}/archive")
    public TripResponse archiveTrip(@PathVariable UUID tripId) {
        UUID currentUserId = currentUserProvider.requireCurrentUserId();
        return tripService.archiveTrip(tripId, currentUserId);
    }

    @PatchMapping("/{tripId}/restore")
    public TripResponse restoreTrip(@PathVariable UUID tripId) {
        UUID currentUserId = currentUserProvider.requireCurrentUserId();
        return tripService.restoreTrip(tripId, currentUserId);
    }

    @DeleteMapping("/{tripId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrip(@PathVariable UUID tripId) {
        UUID currentUserId = currentUserProvider.requireCurrentUserId();
        tripService.deleteTrip(tripId, currentUserId);
    }

    // --- Stop Endpoints ---

    @PostMapping("/{tripId}/stops")
    @ResponseStatus(HttpStatus.CREATED)
    public TripStopResponse addStop(
        @PathVariable UUID tripId,
        @Valid @RequestBody CreateTripStopRequest request
    ) {
        UUID currentUserId = currentUserProvider.requireCurrentUserId();
        return tripService.addStop(tripId, request, currentUserId);
    }

    @PutMapping("/{tripId}/stops/{stopId}")
    public TripStopResponse updateStop(
        @PathVariable UUID tripId,
        @PathVariable UUID stopId,
        @Valid @RequestBody UpdateTripStopRequest request
    ) {
        UUID currentUserId = currentUserProvider.requireCurrentUserId();
        return tripService.updateStop(tripId, stopId, request, currentUserId);
    }

    @DeleteMapping("/{tripId}/stops/{stopId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStop(@PathVariable UUID tripId, @PathVariable UUID stopId) {
        UUID currentUserId = currentUserProvider.requireCurrentUserId();
        tripService.deleteStop(tripId, stopId, currentUserId);
    }

    @PutMapping("/{tripId}/stops/reorder")
    public TripDetailResponse reorderStops(
        @PathVariable UUID tripId,
        @Valid @RequestBody ReorderStopsRequest request
    ) {
        UUID currentUserId = currentUserProvider.requireCurrentUserId();
        return tripService.reorderStops(tripId, request, currentUserId);
    }
}
