package com.citypulse.trip.controller;

import com.citypulse.common.exception.ResourceNotFoundException;
import com.citypulse.trip.entity.Trip;
import com.citypulse.trip.repository.TripRepository;
import com.citypulse.trip.service.CreateTripRequest;
import com.citypulse.trip.service.TripResponse;
import com.citypulse.user.repository.AppUserRepository;
import com.citypulse.user.service.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/trips")
public class TripController {
    private final TripRepository tripRepository;
    private final AppUserRepository userRepository;

    public TripController(TripRepository tripRepository, AppUserRepository userRepository) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public Map<String, Object> listTrips(@AuthenticationPrincipal CurrentUser currentUser) {
        List<TripResponse> trips = tripRepository.findByOwnerIdOrderByCreatedAtDesc(currentUser.id()).stream()
                .map(TripResponse::from)
                .toList();
        return Map.of("items", trips, "total", trips.size());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TripResponse createTrip(
            @AuthenticationPrincipal CurrentUser currentUser,
            @Valid @RequestBody CreateTripRequest request) {
        var owner = userRepository.findById(currentUser.id())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản."));
        Trip trip = tripRepository.save(new Trip(request.title().trim(), owner));
        return TripResponse.from(trip);
    }
}
