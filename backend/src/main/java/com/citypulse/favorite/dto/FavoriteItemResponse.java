package com.citypulse.favorite.dto;

import com.citypulse.favorite.entity.Favorite;
import com.citypulse.place.dto.PlaceSummaryResponse;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for displaying a user's favorited place.
 */
public record FavoriteItemResponse(
    UUID favoriteId,
    Instant favoritedAt,
    PlaceSummaryResponse place
) {
    public static FavoriteItemResponse fromEntity(Favorite favorite) {
        if (favorite == null) {
            return null;
        }
        return new FavoriteItemResponse(
            favorite.getId(),
            favorite.getCreatedAt(),
            PlaceSummaryResponse.fromEntity(favorite.getPlace())
        );
    }
}
