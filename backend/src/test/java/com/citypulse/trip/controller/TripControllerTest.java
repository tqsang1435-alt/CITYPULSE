package com.citypulse.trip.controller;

import com.citypulse.common.security.CurrentUserProvider;
import com.citypulse.trip.dto.CreateTripRequest;
import com.citypulse.trip.dto.TripDetailResponse;
import com.citypulse.trip.dto.TripResponse;
import com.citypulse.trip.dto.UpdateTripRequest;
import com.citypulse.trip.entity.TripStatus;
import com.citypulse.trip.exception.TripAccessDeniedException;
import com.citypulse.trip.exception.TripExceptionHandler;
import com.citypulse.trip.exception.TripNotFoundException;
import com.citypulse.trip.exception.TripValidationException;
import com.citypulse.trip.service.TripService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TripControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TripService tripService;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private TripController tripController;

    private UUID ownerId;
    private UUID tripId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tripController)
            .setControllerAdvice(new TripExceptionHandler())
            .build();

        ownerId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        tripId = UUID.randomUUID();
    }

    // ==========================================
    // 1. POST /api/v1/trips - CREATE TRIP
    // ==========================================

    @Test
    @DisplayName("POST /api/v1/trips - Should return 201 Created on valid input")
    void createTrip_ShouldReturn201_WhenValidInput() throws Exception {
        when(currentUserProvider.requireCurrentUserId()).thenReturn(ownerId);

        TripDetailResponse response = new TripDetailResponse(
            tripId,
            ownerId,
            "Chuyen di Da Nang 3 ngay",
            "Lich trinh cuoi tuan",
            LocalDate.of(2026, 11, 1),
            LocalDate.of(2026, 11, 3),
            new BigDecimal("3000000"),
            TripStatus.DRAFT,
            3,
            0,
            BigDecimal.ZERO,
            List.of(),
            Instant.now(),
            Instant.now()
        );

        when(tripService.createTrip(any(CreateTripRequest.class), eq(ownerId))).thenReturn(response);

        String jsonBody = """
            {
                "title": "Chuyen di Da Nang 3 ngay",
                "description": "Lich trinh cuoi tuan",
                "startDate": "2026-11-01",
                "endDate": "2026-11-03",
                "budget": 3000000
            }
            """;

        mockMvc.perform(post("/api/v1/trips")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(tripId.toString()))
            .andExpect(jsonPath("$.title").value("Chuyen di Da Nang 3 ngay"))
            .andExpect(jsonPath("$.totalDays").value(3))
            .andExpect(jsonPath("$.status").value("DRAFT"));

        verify(tripService).createTrip(any(CreateTripRequest.class), eq(ownerId));
    }

    @Test
    @DisplayName("POST /api/v1/trips - Should return 400 Bad Request when title is blank")
    void createTrip_ShouldReturn400_WhenTitleIsBlank() throws Exception {
        String jsonBody = """
            {
                "title": "   ",
                "startDate": "2026-11-01",
                "endDate": "2026-11-03"
            }
            """;

        mockMvc.perform(post("/api/v1/trips")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"));

        verifyNoInteractions(tripService);
    }

    @Test
    @DisplayName("POST /api/v1/trips - Should return 400 Bad Request when startDate is null")
    void createTrip_ShouldReturn400_WhenStartDateIsNull() throws Exception {
        String jsonBody = """
            {
                "title": "Chuyen di Da Nang",
                "endDate": "2026-11-03"
            }
            """;

        mockMvc.perform(post("/api/v1/trips")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"));

        verifyNoInteractions(tripService);
    }

    @Test
    @DisplayName("POST /api/v1/trips - Should return 400 Bad Request when budget is negative")
    void createTrip_ShouldReturn400_WhenBudgetIsNegative() throws Exception {
        String jsonBody = """
            {
                "title": "Chuyen di Da Nang",
                "startDate": "2026-11-01",
                "endDate": "2026-11-03",
                "budget": -1000
            }
            """;

        mockMvc.perform(post("/api/v1/trips")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"));

        verifyNoInteractions(tripService);
    }

    @Test
    @DisplayName("POST /api/v1/trips - Should return 401 Unauthorized when user context is absent")
    void createTrip_ShouldReturn401_WhenUnauthenticated() throws Exception {
        when(currentUserProvider.requireCurrentUserId())
            .thenThrow(new AuthenticationCredentialsNotFoundException("Authentication required"));

        String jsonBody = """
            {
                "title": "Chuyen di Da Nang",
                "startDate": "2026-11-01",
                "endDate": "2026-11-03"
            }
            """;

        mockMvc.perform(post("/api/v1/trips")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    // ==========================================
    // 2. GET /api/v1/trips - LIST TRIPS
    // ==========================================

    @Test
    @DisplayName("GET /api/v1/trips - Should return 200 OK with list of trips")
    void listTrips_ShouldReturn200_WithTrips() throws Exception {
        when(currentUserProvider.requireCurrentUserId()).thenReturn(ownerId);

        TripResponse t1 = new TripResponse(
            tripId,
            ownerId,
            "Chuyen di 1",
            null,
            LocalDate.of(2026, 11, 1),
            LocalDate.of(2026, 11, 3),
            new BigDecimal("2000000"),
            TripStatus.DRAFT,
            3,
            0,
            Instant.now(),
            Instant.now()
        );

        when(tripService.listTripsByOwner(ownerId, null)).thenReturn(List.of(t1));

        mockMvc.perform(get("/api/v1/trips"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("Chuyen di 1"));

        verify(tripService).listTripsByOwner(ownerId, null);
    }

    @Test
    @DisplayName("GET /api/v1/trips?status=DRAFT - Should return 200 OK with filtered list")
    void listTrips_ShouldReturn200_WithStatusFilter() throws Exception {
        when(currentUserProvider.requireCurrentUserId()).thenReturn(ownerId);
        when(tripService.listTripsByOwner(ownerId, TripStatus.DRAFT)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/trips").param("status", "DRAFT"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));

        verify(tripService).listTripsByOwner(ownerId, TripStatus.DRAFT);
    }

    // ==========================================
    // 3. GET /api/v1/trips/{id} - GET TRIP DETAILS
    // ==========================================

    @Test
    @DisplayName("GET /api/v1/trips/{id} - Should return 200 OK with trip details")
    void getTrip_ShouldReturn200_WhenFound() throws Exception {
        when(currentUserProvider.requireCurrentUserId()).thenReturn(ownerId);

        TripDetailResponse response = new TripDetailResponse(
            tripId,
            ownerId,
            "Chuyen di Da Nang",
            "Mo ta chi tiet",
            LocalDate.of(2026, 11, 1),
            LocalDate.of(2026, 11, 3),
            new BigDecimal("5000000"),
            TripStatus.ACTIVE,
            3,
            0,
            BigDecimal.ZERO,
            List.of(),
            Instant.now(),
            Instant.now()
        );

        when(tripService.getTripById(tripId, ownerId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/trips/{tripId}", tripId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(tripId.toString()))
            .andExpect(jsonPath("$.title").value("Chuyen di Da Nang"))
            .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("GET /api/v1/trips/{id} - Should return 404 Not Found when trip does not exist")
    void getTrip_ShouldReturn404_WhenNotFound() throws Exception {
        when(currentUserProvider.requireCurrentUserId()).thenReturn(ownerId);
        when(tripService.getTripById(tripId, ownerId)).thenThrow(new TripNotFoundException(tripId));

        mockMvc.perform(get("/api/v1/trips/{tripId}", tripId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    @DisplayName("GET /api/v1/trips/{id} - Should return 403 Forbidden when requester is not owner")
    void getTrip_ShouldReturn403_WhenNotOwner() throws Exception {
        when(currentUserProvider.requireCurrentUserId()).thenReturn(ownerId);
        when(tripService.getTripById(tripId, ownerId)).thenThrow(new TripAccessDeniedException(tripId, ownerId));

        mockMvc.perform(get("/api/v1/trips/{tripId}", tripId))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }

    // ==========================================
    // 4. PUT /api/v1/trips/{id} - UPDATE TRIP
    // ==========================================

    @Test
    @DisplayName("PUT /api/v1/trips/{id} - Should return 200 OK on valid update")
    void updateTrip_ShouldReturn200_WhenValid() throws Exception {
        when(currentUserProvider.requireCurrentUserId()).thenReturn(ownerId);

        TripDetailResponse response = new TripDetailResponse(
            tripId,
            ownerId,
            "Tieu de moi",
            "Mo ta moi",
            LocalDate.of(2026, 11, 2),
            LocalDate.of(2026, 11, 5),
            new BigDecimal("6000000"),
            TripStatus.DRAFT,
            4,
            0,
            BigDecimal.ZERO,
            List.of(),
            Instant.now(),
            Instant.now()
        );

        when(tripService.updateTrip(eq(tripId), any(UpdateTripRequest.class), eq(ownerId))).thenReturn(response);

        String jsonBody = """
            {
                "title": "Tieu de moi",
                "description": "Mo ta moi",
                "startDate": "2026-11-02",
                "endDate": "2026-11-05",
                "budget": 6000000
            }
            """;

        mockMvc.perform(put("/api/v1/trips/{tripId}", tripId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Tieu de moi"))
            .andExpect(jsonPath("$.totalDays").value(4));
    }

    @Test
    @DisplayName("PUT /api/v1/trips/{id} - Should return 400 Bad Request when service validation fails")
    void updateTrip_ShouldReturn400_WhenValidationFails() throws Exception {
        when(currentUserProvider.requireCurrentUserId()).thenReturn(ownerId);
        when(tripService.updateTrip(eq(tripId), any(UpdateTripRequest.class), eq(ownerId)))
            .thenThrow(new TripValidationException("Start date cannot be after end date"));

        String jsonBody = """
            {
                "title": "Lich trinh sai ngay",
                "startDate": "2026-11-05",
                "endDate": "2026-11-01"
            }
            """;

        mockMvc.perform(put("/api/v1/trips/{tripId}", tripId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }

    // ==========================================
    // 5. DELETE /api/v1/trips/{id} - DELETE TRIP
    // ==========================================

    @Test
    @DisplayName("DELETE /api/v1/trips/{id} - Should return 204 No Content on successful deletion")
    void deleteTrip_ShouldReturn204_WhenSuccessful() throws Exception {
        when(currentUserProvider.requireCurrentUserId()).thenReturn(ownerId);
        doNothing().when(tripService).deleteTrip(tripId, ownerId);

        mockMvc.perform(delete("/api/v1/trips/{tripId}", tripId))
            .andExpect(status().isNoContent());

        verify(tripService).deleteTrip(tripId, ownerId);
    }

    @Test
    @DisplayName("DELETE /api/v1/trips/{id} - Should return 403 Forbidden when requester is not owner")
    void deleteTrip_ShouldReturn403_WhenNotOwner() throws Exception {
        when(currentUserProvider.requireCurrentUserId()).thenReturn(ownerId);
        doThrow(new TripAccessDeniedException(tripId, ownerId)).when(tripService).deleteTrip(tripId, ownerId);

        mockMvc.perform(delete("/api/v1/trips/{tripId}", tripId))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }
}

