package com.citypulse.place.dto;

import com.citypulse.place.entity.Place;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Summary DTO for displaying places on Discover list and Homepage cards.
 */
public record PlaceSummaryResponse(
    UUID id,
    String slug,
    String name,
    String category,
    String district,
    String address,
    Double latitude,
    Double longitude,
    String thumbnailUrl,
    BigDecimal priceMin,
    BigDecimal priceMax,
    Integer visitDurationMin,
    BigDecimal ratingAverage,
    Integer reviewCount,
    String tags,
    String bestTime
) {
    public static PlaceSummaryResponse fromEntity(Place place) {
        if (place == null) {
            return null;
        }
        return new PlaceSummaryResponse(
            place.getId(),
            place.getSlug(),
            place.getName(),
            place.getCategory(),
            place.getDistrict(),
            place.getAddress(),
            place.getLatitude(),
            place.getLongitude(),
            place.getThumbnailUrl(),
            place.getPriceMin(),
            place.getPriceMax(),
            place.getVisitDurationMin(),
            place.getRatingAverage(),
            place.getReviewCount(),
            place.getTags(),
            place.getBestTime()
        );
    }
}
