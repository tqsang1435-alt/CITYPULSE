package com.citypulse.trip.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TripValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("CreateTripRequest valid data produces no violations")
    void shouldPassValidCreateTripRequest() {
        CreateTripRequest req = new CreateTripRequest(
            "Valid Title",
            "Description",
            LocalDate.of(2026, 11, 1),
            LocalDate.of(2026, 11, 3),
            new BigDecimal("1000000")
        );

        Set<ConstraintViolation<CreateTripRequest>> violations = validator.validate(req);
        assertTrue(violations.isEmpty(), "Expected no violations for valid request");
    }

    @Test
    @DisplayName("CreateTripRequest rejects blank title and null dates")
    void shouldDetectBlankTitleAndNullDates() {
        CreateTripRequest req = new CreateTripRequest(
            "   ",
            null,
            null,
            null,
            null
        );

        Set<ConstraintViolation<CreateTripRequest>> violations = validator.validate(req);
        assertEquals(3, violations.size(), "Expected violations for title, startDate, endDate");
    }

    @Test
    @DisplayName("CreateTripRequest rejects negative budget")
    void shouldDetectNegativeBudget() {
        CreateTripRequest req = new CreateTripRequest(
            "Title",
            null,
            LocalDate.now(),
            LocalDate.now(),
            new BigDecimal("-1.0")
        );

        Set<ConstraintViolation<CreateTripRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("budget")));
    }

    @Test
    @DisplayName("CreateTripStopRequest rejects out-of-range coordinates and dayNumber < 1")
    void shouldDetectInvalidStopParameters() {
        CreateTripStopRequest req = new CreateTripStopRequest(
            null,
            "Place",
            95.0, // Invalid latitude (> 90)
            -200.0, // Invalid longitude (< -180)
            0, // Invalid dayNumber (< 1)
            -1, // Invalid orderIndex (< 0)
            null,
            null,
            new BigDecimal("-50"), // Negative cost
            null
        );

        Set<ConstraintViolation<CreateTripStopRequest>> violations = validator.validate(req);
        assertEquals(5, violations.size());
    }
}

