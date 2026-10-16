package com.citypulse.trip.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TripTest {

    @Test
    @DisplayName("Trip totalDays calculates correct inclusive number of days")
    void shouldCalculateTotalDaysCorrectly() {
        Trip singleDay = new Trip(UUID.randomUUID(), "Single Day", LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 1));
        assertEquals(1, singleDay.getTotalDays());

        Trip threeDays = new Trip(UUID.randomUUID(), "Three Days", LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 3));
        assertEquals(3, threeDays.getTotalDays());
    }

    @Test
    @DisplayName("Trip areDatesValid correctly validates startDate <= endDate")
    void shouldValidateDateOrdering() {
        Trip valid = new Trip(UUID.randomUUID(), "Valid", LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 5));
        assertTrue(valid.areDatesValid());

        Trip invalid = new Trip(UUID.randomUUID(), "Invalid", LocalDate.of(2026, 11, 5), LocalDate.of(2026, 11, 1));
        assertFalse(invalid.areDatesValid());
    }

    @Test
    @DisplayName("archive() transitions active trip to ARCHIVED and rejects already archived trip")
    void shouldHandleArchiveLifecycle() {
        Trip trip = new Trip(UUID.randomUUID(), "Trip", LocalDate.now(), LocalDate.now().plusDays(2));
        trip.setStatus(TripStatus.ACTIVE);

        trip.archive();
        assertEquals(TripStatus.ARCHIVED, trip.getStatus());

        assertThrows(IllegalStateException.class, trip::archive, "Cannot archive an already archived trip");
    }

    @Test
    @DisplayName("restore() transitions ARCHIVED trip to ACTIVE and rejects non-archived trip")
    void shouldHandleRestoreLifecycle() {
        Trip trip = new Trip(UUID.randomUUID(), "Trip", LocalDate.now(), LocalDate.now().plusDays(2));
        trip.setStatus(TripStatus.ARCHIVED);

        trip.restore();
        assertEquals(TripStatus.ACTIVE, trip.getStatus());

        assertThrows(IllegalStateException.class, trip::restore, "Cannot restore a non-archived trip");
    }

    @Test
    @DisplayName("PrePersist sets default status to DRAFT and initializes timestamps")
    void shouldSetDefaultsOnPrePersist() {
        Trip trip = new Trip();
        trip.setStartDate(LocalDate.now());
        trip.setEndDate(LocalDate.now());

        trip.setStatus(null);
        assertNull(trip.getCreatedAt());
        assertNull(trip.getStatus());

        trip.onCreate();

        assertNotNull(trip.getCreatedAt());
        assertNotNull(trip.getUpdatedAt());
        assertEquals(TripStatus.DRAFT, trip.getStatus());
    }

    @Test
    @DisplayName("addStop and removeStop manage bidirectional relationship")
    void shouldManageBidirectionalStopRelationship() {
        Trip trip = new Trip(UUID.randomUUID(), "Trip", LocalDate.now(), LocalDate.now());
        TripStop stop = new TripStop();
        stop.setCustomName("Dragon Bridge");

        trip.addStop(stop);
        assertEquals(1, trip.getStops().size());
        assertSame(trip, stop.getTrip());

        trip.removeStop(stop);
        assertEquals(0, trip.getStops().size());
        assertNull(stop.getTrip());
    }

    @Test
    @DisplayName("Entity equality is based on UUID identity")
    void shouldHandleEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        Trip t1 = new Trip();
        t1.setId(id);

        Trip t2 = new Trip();
        t2.setId(id);

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }
}
