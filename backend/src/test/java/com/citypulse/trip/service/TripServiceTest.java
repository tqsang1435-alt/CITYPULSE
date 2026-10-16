package com.citypulse.trip.service;

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
import com.citypulse.trip.service.impl.TripServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private TripStopRepository tripStopRepository;

    @InjectMocks
    private TripServiceImpl tripService;

    private UUID ownerId;
    private UUID tripId;
    private Trip sampleTrip;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        tripId = UUID.randomUUID();

        sampleTrip = new Trip();
        sampleTrip.setId(tripId);
        sampleTrip.setOwnerId(ownerId);
        sampleTrip.setTitle("Danang 3-Day Getaway");
        sampleTrip.setDescription("Exploring Danang coast and food");
        sampleTrip.setStartDate(LocalDate.of(2026, 11, 1));
        sampleTrip.setEndDate(LocalDate.of(2026, 11, 3));
        sampleTrip.setBudget(new BigDecimal("2000000.00"));
        sampleTrip.setStatus(TripStatus.ACTIVE);
        sampleTrip.setStops(new ArrayList<>());
    }

    @Test
    @DisplayName("createTrip creates trip successfully with valid data")
    void shouldCreateTripSuccessfully() {
        CreateTripRequest req = new CreateTripRequest(
            "Weekend in Danang",
            "Beach and seafood",
            LocalDate.of(2026, 11, 1),
            LocalDate.of(2026, 11, 2),
            new BigDecimal("1500000")
        );

        when(tripRepository.save(any(Trip.class))).thenAnswer(invocation -> {
            Trip t = invocation.getArgument(0);
            t.setId(UUID.randomUUID());
            return t;
        });

        TripDetailResponse response = tripService.createTrip(req, ownerId);

        assertNotNull(response);
        assertEquals("Weekend in Danang", response.title());
        assertEquals(2, response.totalDays());
        assertEquals(TripStatus.DRAFT, response.status());
        verify(tripRepository, times(1)).save(any(Trip.class));
    }

    @Test
    @DisplayName("createTrip throws TripValidationException when startDate is after endDate")
    void shouldRejectTripWhenStartDateAfterEndDate() {
        CreateTripRequest req = new CreateTripRequest(
            "Invalid Dates Trip",
            null,
            LocalDate.of(2026, 11, 5),
            LocalDate.of(2026, 11, 1),
            null
        );

        TripValidationException ex = assertThrows(TripValidationException.class, () -> {
            tripService.createTrip(req, ownerId);
        });

        assertTrue(ex.getMessage().contains("cannot be after end date"));
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    @DisplayName("createTrip throws TripValidationException when budget is negative")
    void shouldRejectTripWithNegativeBudget() {
        CreateTripRequest req = new CreateTripRequest(
            "Negative Budget Trip",
            null,
            LocalDate.of(2026, 11, 1),
            LocalDate.of(2026, 11, 2),
            new BigDecimal("-100")
        );

        assertThrows(TripValidationException.class, () -> {
            tripService.createTrip(req, ownerId);
        });
    }

    @Test
    @DisplayName("getTripById returns trip when requester is the owner")
    void shouldReturnTripWhenRequesterIsOwner() {
        when(tripRepository.findByIdWithStops(tripId)).thenReturn(Optional.of(sampleTrip));

        TripDetailResponse response = tripService.getTripById(tripId, ownerId);

        assertNotNull(response);
        assertEquals(tripId, response.id());
        assertEquals(ownerId, response.ownerId());
    }

    @Test
    @DisplayName("getTripById throws TripNotFoundException when trip does not exist")
    void shouldThrowNotFoundWhenTripDoesNotExist() {
        UUID unknownId = UUID.randomUUID();
        when(tripRepository.findByIdWithStops(unknownId)).thenReturn(Optional.empty());

        assertThrows(TripNotFoundException.class, () -> {
            tripService.getTripById(unknownId, ownerId);
        });
    }

    @Test
    @DisplayName("getTripById throws TripAccessDeniedException when requester is not the owner")
    void shouldThrowAccessDeniedWhenRequesterIsNotOwner() {
        UUID unauthorizedUser = UUID.randomUUID();
        when(tripRepository.findByIdWithStops(tripId)).thenReturn(Optional.of(sampleTrip));

        assertThrows(TripAccessDeniedException.class, () -> {
            tripService.getTripById(tripId, unauthorizedUser);
        });
    }

    @Test
    @DisplayName("updateTrip updates details successfully")
    void shouldUpdateTripDetails() {
        UpdateTripRequest req = new UpdateTripRequest(
            "Updated Title",
            "Updated Description",
            LocalDate.of(2026, 11, 1),
            LocalDate.of(2026, 11, 4),
            new BigDecimal("3000000")
        );

        when(tripRepository.findByIdWithStops(tripId)).thenReturn(Optional.of(sampleTrip));
        when(tripRepository.save(any(Trip.class))).thenAnswer(i -> i.getArgument(0));

        TripDetailResponse updated = tripService.updateTrip(tripId, req, ownerId);

        assertEquals("Updated Title", updated.title());
        assertEquals(4, updated.totalDays());
    }

    @Test
    @DisplayName("updateTrip rejects shortening duration when existing stops fall outside new window")
    void shouldRejectTripShorteningWhenStopsConflict() {
        TripStop stopOnDay3 = new TripStop();
        stopOnDay3.setId(UUID.randomUUID());
        stopOnDay3.setCustomName("Ba Na Hills");
        stopOnDay3.setDayNumber(3);
        sampleTrip.getStops().add(stopOnDay3);

        when(tripRepository.findByIdWithStops(tripId)).thenReturn(Optional.of(sampleTrip));

        // Attempt to shorten from 3 days to 2 days (Nov 1 to Nov 2)
        UpdateTripRequest req = new UpdateTripRequest(
            "Shortened Trip",
            null,
            LocalDate.of(2026, 11, 1),
            LocalDate.of(2026, 11, 2),
            null
        );

        TripValidationException ex = assertThrows(TripValidationException.class, () -> {
            tripService.updateTrip(tripId, req, ownerId);
        });

        assertTrue(ex.getMessage().contains("Cannot shorten trip to 2 days"));
        assertTrue(ex.getMessage().contains("Ba Na Hills"));
    }

    @Test
    @DisplayName("archiveTrip transitions trip to ARCHIVED")
    void shouldArchiveTrip() {
        sampleTrip.setStatus(TripStatus.ACTIVE);
        when(tripRepository.findByIdWithStops(tripId)).thenReturn(Optional.of(sampleTrip));
        when(tripRepository.save(any(Trip.class))).thenAnswer(i -> i.getArgument(0));

        TripResponse response = tripService.archiveTrip(tripId, ownerId);

        assertEquals(TripStatus.ARCHIVED, response.status());
    }

    @Test
    @DisplayName("restoreTrip transitions ARCHIVED trip back to ACTIVE")
    void shouldRestoreTrip() {
        sampleTrip.setStatus(TripStatus.ARCHIVED);
        when(tripRepository.findByIdWithStops(tripId)).thenReturn(Optional.of(sampleTrip));
        when(tripRepository.save(any(Trip.class))).thenAnswer(i -> i.getArgument(0));

        TripResponse response = tripService.restoreTrip(tripId, ownerId);

        assertEquals(TripStatus.ACTIVE, response.status());
    }

    @Test
    @DisplayName("addStop adds a valid stop to the trip")
    void shouldAddStopSuccessfully() {
        when(tripRepository.findByIdWithStops(tripId)).thenReturn(Optional.of(sampleTrip));
        when(tripStopRepository.findMaxOrderIndexByTripIdAndDayNumber(tripId, 1)).thenReturn(-1);
        when(tripStopRepository.save(any(TripStop.class))).thenAnswer(i -> {
            TripStop s = i.getArgument(0);
            s.setId(UUID.randomUUID());
            return s;
        });

        CreateTripStopRequest req = new CreateTripStopRequest(
            null,
            "Linh Ung Pagoda",
            16.1000,
            108.2770,
            1,
            null,
            LocalTime.of(8, 30),
            LocalTime.of(10, 0),
            new BigDecimal("50000"),
            "Morning visit"
        );

        TripStopResponse stopResponse = tripService.addStop(tripId, req, ownerId);

        assertNotNull(stopResponse);
        assertEquals("Linh Ung Pagoda", stopResponse.customName());
        assertEquals(1, stopResponse.dayNumber());
        assertEquals(0, stopResponse.orderIndex());
    }

    @Test
    @DisplayName("addStop rejects stop when dayNumber exceeds trip duration")
    void shouldRejectStopExceedingTripDuration() {
        when(tripRepository.findByIdWithStops(tripId)).thenReturn(Optional.of(sampleTrip));

        // Sample trip is 3 days (Nov 1 to Nov 3). Day 5 is invalid.
        CreateTripStopRequest req = new CreateTripStopRequest(
            null,
            "Far Out Stop",
            null,
            null,
            5,
            0,
            null,
            null,
            null,
            null
        );

        TripValidationException ex = assertThrows(TripValidationException.class, () -> {
            tripService.addStop(tripId, req, ownerId);
        });

        assertTrue(ex.getMessage().contains("exceeds total trip duration"));
    }

    @Test
    @DisplayName("addStop rejects stop when neither placeId nor customName is provided")
    void shouldRejectStopWithoutPlaceIdOrCustomName() {
        when(tripRepository.findByIdWithStops(tripId)).thenReturn(Optional.of(sampleTrip));

        CreateTripStopRequest req = new CreateTripStopRequest(
            null,
            "   ",
            null,
            null,
            1,
            0,
            null,
            null,
            null,
            null
        );

        assertThrows(TripValidationException.class, () -> {
            tripService.addStop(tripId, req, ownerId);
        });
    }

    @Test
    @DisplayName("addStop rejects invalid coordinates")
    void shouldRejectInvalidCoordinates() {
        when(tripRepository.findByIdWithStops(tripId)).thenReturn(Optional.of(sampleTrip));

        CreateTripStopRequest req = new CreateTripStopRequest(
            null,
            "Out of bounds",
            150.0,
            108.0,
            1,
            0,
            null,
            null,
            null,
            null
        );

        assertThrows(TripValidationException.class, () -> {
            tripService.addStop(tripId, req, ownerId);
        });
    }

    @Test
    @DisplayName("addStop rejects arrival time after departure time")
    void shouldRejectArrivalTimeAfterDeparture() {
        when(tripRepository.findByIdWithStops(tripId)).thenReturn(Optional.of(sampleTrip));

        CreateTripStopRequest req = new CreateTripStopRequest(
            null,
            "Time Travel Stop",
            null,
            null,
            1,
            0,
            LocalTime.of(15, 0),
            LocalTime.of(14, 0),
            null,
            null
        );

        assertThrows(TripValidationException.class, () -> {
            tripService.addStop(tripId, req, ownerId);
        });
    }

    @Test
    @DisplayName("deleteStop deletes stop successfully")
    void shouldDeleteStopSuccessfully() {
        UUID stopId = UUID.randomUUID();
        TripStop stop = new TripStop();
        stop.setId(stopId);
        stop.setTrip(sampleTrip);
        sampleTrip.getStops().add(stop);

        when(tripRepository.findByIdWithStops(tripId)).thenReturn(Optional.of(sampleTrip));
        when(tripStopRepository.findByIdAndTripId(stopId, tripId)).thenReturn(Optional.of(stop));

        tripService.deleteStop(tripId, stopId, ownerId);

        verify(tripStopRepository, times(1)).delete(stop);
        assertFalse(sampleTrip.getStops().contains(stop));
    }

    @Test
    @DisplayName("reorderStops updates day numbers and order indices")
    void shouldReorderStopsSuccessfully() {
        UUID stop1Id = UUID.randomUUID();
        UUID stop2Id = UUID.randomUUID();

        TripStop stop1 = new TripStop(sampleTrip, "Stop 1", 1, 0);
        stop1.setId(stop1Id);

        TripStop stop2 = new TripStop(sampleTrip, "Stop 2", 1, 1);
        stop2.setId(stop2Id);

        when(tripRepository.findByIdWithStops(tripId)).thenReturn(Optional.of(sampleTrip));
        when(tripStopRepository.findByIdAndTripId(stop1Id, tripId)).thenReturn(Optional.of(stop1));
        when(tripStopRepository.findByIdAndTripId(stop2Id, tripId)).thenReturn(Optional.of(stop2));

        ReorderStopsRequest request = new ReorderStopsRequest(List.of(
            new ReorderStopsRequest.StopOrderItem(stop1Id, 1, 1),
            new ReorderStopsRequest.StopOrderItem(stop2Id, 1, 0)
        ));

        tripService.reorderStops(tripId, request, ownerId);

        assertEquals(1, stop1.getOrderIndex());
        assertEquals(0, stop2.getOrderIndex());
        verify(tripStopRepository, times(2)).save(any(TripStop.class));
    }
}

