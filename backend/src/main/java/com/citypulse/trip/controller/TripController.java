package com.citypulse.trip.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/trips")
public class TripController {
    @GetMapping
    public Map<String, Object> listTrips() {
        // TODO: require authentication and enforce Trip membership privacy.
        return Map.of("items", List.of(), "total", 0);
    }
}
