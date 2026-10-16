package com.citypulse.trip.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TripStopTest {

    @Test
    @DisplayName("areCoordinatesValid validates latitude and longitude boundaries")
    void shouldValidateCoordinates() {
        TripStop valid = new TripStop();
        valid.setLatitude(16.0678);
        valid.setLongitude(108.2208);
        assertTrue(valid.areCoordinatesValid());

        TripStop invalidLat = new TripStop();
        invalidLat.setLatitude(95.0);
        invalidLat.setLongitude(108.0);
        assertFalse(invalidLat.areCoordinatesValid());

        TripStop invalidLng = new TripStop();
        invalidLng.setLatitude(16.0);
        invalidLng.setLongitude(-190.0);
        assertFalse(invalidLng.areCoordinatesValid());

        TripStop nullCoords = new TripStop();
        assertTrue(nullCoords.areCoordinatesValid(), "Null coordinates should be considered valid (optional)");
    }

    @Test
    @DisplayName("areTimesValid validates that arrivalTime is not after departureTime")
    void shouldValidateArrivalAndDepartureTimes() {
        TripStop valid = new TripStop();
        valid.setArrivalTime(LocalTime.of(9, 0));
        valid.setDepartureTime(LocalTime.of(10, 30));
        assertTrue(valid.areTimesValid());

        TripStop sameTime = new TripStop();
        sameTime.setArrivalTime(LocalTime.of(9, 0));
        sameTime.setDepartureTime(LocalTime.of(9, 0));
        assertTrue(sameTime.areTimesValid());

        TripStop invalid = new TripStop();
        invalid.setArrivalTime(LocalTime.of(11, 0));
        invalid.setDepartureTime(LocalTime.of(10, 0));
        assertFalse(invalid.areTimesValid());

        TripStop oneNull = new TripStop();
        oneNull.setArrivalTime(LocalTime.of(9, 0));
        assertTrue(oneNull.areTimesValid());
    }

    @Test
    @DisplayName("PrePersist sets default dayNumber to 1 and orderIndex to 0")
    void shouldSetDefaultsOnPrePersist() {
        TripStop stop = new TripStop();
        stop.setCustomName("My Khe Beach");

        stop.onCreate();

        assertNotNull(stop.getCreatedAt());
        assertNotNull(stop.getUpdatedAt());
        assertEquals(1, stop.getDayNumber());
        assertEquals(0, stop.getOrderIndex());
    }

    @Test
    @DisplayName("TripStop equality is based on UUID identity")
    void shouldHandleEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        TripStop s1 = new TripStop();
        s1.setId(id);

        TripStop s2 = new TripStop();
        s2.setId(id);

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }
}

