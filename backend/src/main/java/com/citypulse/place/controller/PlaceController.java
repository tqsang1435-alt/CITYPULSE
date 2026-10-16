package com.citypulse.place.controller;

import com.citypulse.common.dto.PageResponse;
import com.citypulse.place.dto.PlaceDetailResponse;
import com.citypulse.place.dto.PlaceSummaryResponse;
import com.citypulse.place.service.PlaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/places")
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    /**
     * Lists published places with pagination, searching, filtering, and sorting.
     * Public endpoint.
     */
    @GetMapping
    public ResponseEntity<PageResponse<PlaceSummaryResponse>> listPlaces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Boolean openNow
    ) {
        PageResponse<PlaceSummaryResponse> response = placeService.listPublishedPlaces(
            page, size, q, category, district, minPrice, maxPrice, minRating, sort, openNow
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves details of a published place by either UUID or slug.
     * Public endpoint.
     */
    @GetMapping("/{idOrSlug}")
    public ResponseEntity<PlaceDetailResponse> getPlaceDetail(@PathVariable String idOrSlug) {
        PlaceDetailResponse response = placeService.getPlaceByIdOrSlug(idOrSlug);
        return ResponseEntity.ok(response);
    }
}
