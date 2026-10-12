package com.citypulse.place.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/places")
public class PlaceController {
    @GetMapping
    public Map<String, Object> listPublishedPlaces() {
        // TODO: query only PUBLISHED places inside the configured Da Nang polygon.
        return Map.of("items", List.of(), "total", 0);
    }
}
