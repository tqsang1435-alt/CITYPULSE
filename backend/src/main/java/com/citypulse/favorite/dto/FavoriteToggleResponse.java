package com.citypulse.favorite.dto;

import java.util.UUID;

/**
 * DTO returned after adding or removing a place from favorites.
 */
public record FavoriteToggleResponse(
    UUID placeId,
    boolean isFavorite,
    String message
) {
}
