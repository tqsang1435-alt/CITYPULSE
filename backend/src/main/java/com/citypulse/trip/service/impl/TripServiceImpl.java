package com.citypulse.trip.service.impl;

import com.citypulse.trip.dto.CreateTripRequest;
import com.citypulse.trip.dto.CreateTripStopRequest;
import com.citypulse.trip.dto.ReorderStopsRequest;
import com.citypulse.trip.dto.TripDetailResponse;
import com.citypulse.trip.dto.TripResponse;
import com.citypulse.trip.dto.TripStopResponse;
import com.citypulse.trip.dto.UpdateTripRequest;
import com.citypulse.trip.dto.UpdateTripStopRequest;
import com.citypulse.trip.entity.Trip;
import com.citypulse.trip.entity.TripStatus;
import com.citypulse.trip.entity.TripStop;
import com.citypulse.trip.exception.TripAccessDeniedException;
import com.citypulse.trip.exception.TripNotFoundException;
import com.citypulse.trip.exception.TripStopNotFoundException;
import com.citypulse.trip.exception.TripValidationException;
import com.citypulse.trip.repository.TripRepository;
import com.citypulse.trip.repository.TripStopRepository;
import com.citypulse.trip.service.TripService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final TripStopRepository tripStopRepository;

    public TripServiceImpl(TripRepository tripRepository, TripStopRepository tripStopRepository) {
        this.tripRepository = tripRepository;
        this.tripStopRepository = tripStopRepository;
    }

    @Override
    public TripDetailResponse createTrip(CreateTripRequest request, UUID ownerId) {
        if (ownerId == null) {
            throw new TripValidationException("Owner ID is required to create a trip");
        }
        validateDates(request.startDate(), request.endDate());
        validateBudget(request.budget());

        Trip trip = new Trip();
        trip.setOwnerId(ownerId);
        trip.setTitle(request.title().trim());
        trip.setDescription(request.description() != null ? request.description().trim() : null);
        trip.setStartDate(request.startDate());
        trip.setEndDate(request.endDate());
        trip.setBudget(request.budget());
        trip.setStatus(TripStatus.DRAFT);

        Trip saved = tripRepository.save(trip);
        return TripDetailResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TripDetailResponse getTripById(UUID tripId, UUID requesterId) {
        Trip trip = findTripAndCheckOwnership(tripId, requesterId);
        return TripDetailResponse.fromEntity(trip);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripResponse> listTripsByOwner(UUID ownerId, TripStatus status) {
        if (ownerId == null) {
            throw new TripValidationException("Owner ID cannot be null");
        }
        List<Trip> trips = (status != null)
            ? tripRepository.findAllByOwnerIdAndStatusOrderByStartDateDesc(ownerId, status)
            : tripRepository.findAllByOwnerIdOrderByStartDateDesc(ownerId);

        return trips.stream()
            .map(TripResponse::fromEntity)
            .toList();
    }

    @Override
    public TripDetailResponse updateTrip(UUID tripId, UpdateTripRequest request, UUID requesterId) {
        Trip trip = findTripAndCheckOwnership(tripId, requesterId);
        validateDates(request.startDate(), request.endDate());
        validateBudget(request.budget());

        // Validate stop consistency: if duration is shortened, ensure no existing stops fall outside the new window
        long newTotalDays = ChronoUnit.DAYS.between(request.startDate(), request.endDate()) + 1;
        if (trip.getStops() != null) {
            for (TripStop stop : trip.getStops()) {
                if (stop.getDayNumber() != null && stop.getDayNumber() > newTotalDays) {
                    throw new TripValidationException(
                        "Cannot shorten trip to " + newTotalDays + " days because stop '"
                        + (stop.getCustomName() != null ? stop.getCustomName() : stop.getId())
                        + "' is currently scheduled on day " + stop.getDayNumber()
                        + ". Please delete or reschedule this stop first."
                    );
                }
            }
        }

        trip.setTitle(request.title().trim());
        trip.setDescription(request.description() != null ? request.description().trim() : null);
        trip.setStartDate(request.startDate());
        trip.setEndDate(request.endDate());
        trip.setBudget(request.budget());

        Trip saved = tripRepository.save(trip);
        return TripDetailResponse.fromEntity(saved);
    }

    @Override
    public TripResponse archiveTrip(UUID tripId, UUID requesterId) {
        Trip trip = findTripAndCheckOwnership(tripId, requesterId);
        try {
            trip.archive();
        } catch (IllegalStateException ex) {
            throw new TripValidationException(ex.getMessage());
        }
        Trip saved = tripRepository.save(trip);
        return TripResponse.fromEntity(saved);
    }

    @Override
    public TripResponse restoreTrip(UUID tripId, UUID requesterId) {
        Trip trip = findTripAndCheckOwnership(tripId, requesterId);
        try {
            trip.restore();
        } catch (IllegalStateException ex) {
            throw new TripValidationException(ex.getMessage());
        }
        Trip saved = tripRepository.save(trip);
        return TripResponse.fromEntity(saved);
    }

    @Override
    public void deleteTrip(UUID tripId, UUID requesterId) {
        Trip trip = findTripAndCheckOwnership(tripId, requesterId);
        tripRepository.delete(trip);
    }

    @Override
    public TripStopResponse addStop(UUID tripId, CreateTripStopRequest request, UUID requesterId) {
        Trip trip = findTripAndCheckOwnership(tripId, requesterId);
        validateStopIdentity(request.placeId(), request.customName());

        int dayNumber = (request.dayNumber() != null) ? request.dayNumber() : 1;
        validateDayNumberAgainstTrip(trip, dayNumber);
        validateCoordinates(request.latitude(), request.longitude());
        validateTimes(request.arrivalTime(), request.departureTime());
        validateBudget(request.estimatedCost());

        int orderIndex;
        if (request.orderIndex() != null && request.orderIndex() >= 0) {
            orderIndex = request.orderIndex();
        } else {
            int maxOrder = tripStopRepository.findMaxOrderIndexByTripIdAndDayNumber(tripId, dayNumber);
            orderIndex = maxOrder + 1;
        }

        TripStop stop = new TripStop();
        stop.setTrip(trip);
        stop.setPlaceId(request.placeId());
        stop.setCustomName(request.customName() != null ? request.customName().trim() : null);
        stop.setLatitude(request.latitude());
        stop.setLongitude(request.longitude());
        stop.setDayNumber(dayNumber);
        stop.setOrderIndex(orderIndex);
        stop.setArrivalTime(request.arrivalTime());
        stop.setDepartureTime(request.departureTime());
        stop.setEstimatedCost(request.estimatedCost());
        stop.setNote(request.note() != null ? request.note().trim() : null);

        TripStop saved = tripStopRepository.save(stop);
        trip.getStops().add(saved);

        return TripStopResponse.fromEntity(saved);
    }

    @Override
    public TripStopResponse updateStop(UUID tripId, UUID stopId, UpdateTripStopRequest request, UUID requesterId) {
        Trip trip = findTripAndCheckOwnership(tripId, requesterId);
        TripStop stop = tripStopRepository.findByIdAndTripId(stopId, tripId)
            .orElseThrow(() -> new TripStopNotFoundException(stopId));

        validateStopIdentity(request.placeId(), request.customName());
        int dayNumber = (request.dayNumber() != null) ? request.dayNumber() : stop.getDayNumber();
        validateDayNumberAgainstTrip(trip, dayNumber);
        validateCoordinates(request.latitude(), request.longitude());
        validateTimes(request.arrivalTime(), request.departureTime());
        validateBudget(request.estimatedCost());

        stop.setPlaceId(request.placeId());
        stop.setCustomName(request.customName() != null ? request.customName().trim() : null);
        stop.setLatitude(request.latitude());
        stop.setLongitude(request.longitude());
        stop.setDayNumber(dayNumber);
        if (request.orderIndex() != null && request.orderIndex() >= 0) {
            stop.setOrderIndex(request.orderIndex());
        }
        stop.setArrivalTime(request.arrivalTime());
        stop.setDepartureTime(request.departureTime());
        stop.setEstimatedCost(request.estimatedCost());
        stop.setNote(request.note() != null ? request.note().trim() : null);

        TripStop saved = tripStopRepository.save(stop);
        return TripStopResponse.fromEntity(saved);
    }

    @Override
    public void deleteStop(UUID tripId, UUID stopId, UUID requesterId) {
        Trip trip = findTripAndCheckOwnership(tripId, requesterId);
        TripStop stop = tripStopRepository.findByIdAndTripId(stopId, tripId)
            .orElseThrow(() -> new TripStopNotFoundException(stopId));

        trip.getStops().remove(stop);
        tripStopRepository.delete(stop);
    }

    @Override
    public TripDetailResponse reorderStops(UUID tripId, ReorderStopsRequest request, UUID requesterId) {
        Trip trip = findTripAndCheckOwnership(tripId, requesterId);

        for (ReorderStopsRequest.StopOrderItem item : request.items()) {
            TripStop stop = tripStopRepository.findByIdAndTripId(item.stopId(), tripId)
                .orElseThrow(() -> new TripStopNotFoundException(item.stopId()));

            validateDayNumberAgainstTrip(trip, item.dayNumber());
            if (item.orderIndex() < 0) {
                throw new TripValidationException("Order index must be >= 0");
            }

            stop.setDayNumber(item.dayNumber());
            stop.setOrderIndex(item.orderIndex());
            tripStopRepository.save(stop);
        }

        Trip updatedTrip = tripRepository.findByIdWithStops(tripId)
            .orElse(trip);
        return TripDetailResponse.fromEntity(updatedTrip);
    }

    // Helper validation methods

    private Trip findTripAndCheckOwnership(UUID tripId, UUID requesterId) {
        Trip trip = tripRepository.findByIdWithStops(tripId)
            .orElseThrow(() -> new TripNotFoundException(tripId));

        if (requesterId != null && !Objects.equals(trip.getOwnerId(), requesterId)) {
            throw new TripAccessDeniedException(tripId, requesterId);
        }
        return trip;
    }

    private void validateDates(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new TripValidationException("Start date and end date are both required");
        }
        if (startDate.isAfter(endDate)) {
            throw new TripValidationException("Start date (" + startDate + ") cannot be after end date (" + endDate + ")");
        }
    }

    private void validateBudget(BigDecimal budget) {
        if (budget != null && budget.compareTo(BigDecimal.ZERO) < 0) {
            throw new TripValidationException("Amount/budget must not be negative: " + budget);
        }
    }

    private void validateDayNumberAgainstTrip(Trip trip, int dayNumber) {
        if (dayNumber < 1) {
            throw new TripValidationException("Day number must be at least 1");
        }
        long totalDays = trip.getTotalDays();
        if (dayNumber > totalDays) {
            throw new TripValidationException(
                "Day number " + dayNumber + " exceeds total trip duration (" + totalDays + " day(s))"
            );
        }
    }

    private void validateStopIdentity(UUID placeId, String customName) {
        boolean hasPlaceId = placeId != null;
        boolean hasCustomName = customName != null && !customName.trim().isEmpty();
        if (!hasPlaceId && !hasCustomName) {
            throw new TripValidationException("A stop must provide either a placeId or a customName");
        }
    }

    private void validateCoordinates(Double latitude, Double longitude) {
        if (latitude != null && (latitude < -90.0 || latitude > 90.0)) {
            throw new TripValidationException("Latitude must be between -90.0 and 90.0: " + latitude);
        }
        if (longitude != null && (longitude < -180.0 || longitude > 180.0)) {
            throw new TripValidationException("Longitude must be between -180.0 and 180.0: " + longitude);
        }
    }

    private void validateTimes(java.time.LocalTime arrivalTime, java.time.LocalTime departureTime) {
        if (arrivalTime != null && departureTime != null && arrivalTime.isAfter(departureTime)) {
            throw new TripValidationException(
                "Arrival time (" + arrivalTime + ") cannot be after departure time (" + departureTime + ")"
            );
        }
    }
}

