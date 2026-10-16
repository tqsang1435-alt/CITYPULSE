package com.citypulse.place.dto;

import com.citypulse.place.entity.Place;
import com.citypulse.place.entity.PlaceStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Detailed DTO for displaying full information on Place Detail page.
 */
public record PlaceDetailResponse(
    UUID id,
    String slug,
    String name,
    String category,
    String district,
    String address,
    Double latitude,
    Double longitude,
    PlaceStatus status,
    String shortDescription,
    String longDescription,
    String thumbnailUrl,
    String coverImageUrl,
    BigDecimal priceMin,
    BigDecimal priceMax,
    Integer visitDurationMin,
    BigDecimal ratingAverage,
    Integer reviewCount,
    String tags,
    String bestTime,
    String dataQuality,
    Instant createdAt,
    Instant updatedAt
) {
    public static PlaceDetailResponse fromEntity(Place place) {
        if (place == null) {
            return null;
        }
        return new PlaceDetailResponse(
            place.getId(),
            place.getSlug(),
            place.getName(),
            place.getCategory(),
            place.getDistrict(),
            place.getAddress(),
            place.getLatitude(),
            place.getLongitude(),
            place.getStatus(),
            place.getShortDescription(),
            place.getLongDescription(),
            place.getThumbnailUrl(),
            place.getCoverImageUrl(),
            place.getPriceMin(),
            place.getPriceMax(),
            place.getVisitDurationMin(),
            place.getRatingAverage(),
            place.getReviewCount(),
            place.getTags(),
            place.getBestTime(),
            place.getDataQuality(),
            place.getCreatedAt(),
            place.getUpdatedAt()
        );
    }
}
