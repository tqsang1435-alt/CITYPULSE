package com.citypulse.favorite.controller;

import com.citypulse.common.dto.PageResponse;
import com.citypulse.favorite.dto.FavoriteItemResponse;
import com.citypulse.favorite.dto.FavoriteToggleResponse;
import com.citypulse.favorite.service.FavoriteService;
import com.citypulse.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /**
     * POST /api/v1/favorites/{placeId}
     * Adds a place to the authenticated user's favorites.
     */
    @PostMapping("/{placeId}")
    public ResponseEntity<FavoriteToggleResponse> addFavorite(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String placeId
    ) {
        FavoriteToggleResponse response = favoriteService.addFavorite(principal.getId(), placeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * DELETE /api/v1/favorites/{placeId}
     * Removes a place from the authenticated user's favorites.
     */
    @DeleteMapping("/{placeId}")
    public ResponseEntity<FavoriteToggleResponse> removeFavorite(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String placeId
    ) {
        FavoriteToggleResponse response = favoriteService.removeFavorite(principal.getId(), placeId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/favorites
     * Retrieves paginated list of favorited places for current authenticated user.
     */
    @GetMapping
    public ResponseEntity<PageResponse<FavoriteItemResponse>> listFavorites(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageResponse<FavoriteItemResponse> response = favoriteService.listFavorites(principal.getId(), page, size);
        return ResponseEntity.ok(response);
    }
}
