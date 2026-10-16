import { apiDelete, apiGet, apiPost } from "../lib/apiClient";
import { FavoriteItemResponse, FavoriteToggleResponse, PageResponse } from "../types/api";

export const favoriteService = {
  /**
   * Adds a place to current user's favorites.
   * Requires Bearer JWT.
   */
  async addFavorite(placeId: string): Promise<FavoriteToggleResponse> {
    return apiPost<FavoriteToggleResponse>(`/favorites/${placeId}`);
  },

  /**
   * Removes a place from current user's favorites.
   * Requires Bearer JWT.
   */
  async removeFavorite(placeId: string): Promise<FavoriteToggleResponse> {
    return apiDelete<FavoriteToggleResponse>(`/favorites/${placeId}`);
  },

  /**
   * Retrieves paginated list of favorited places for current authenticated user.
   */
  async listFavorites(page = 0, size = 50): Promise<PageResponse<FavoriteItemResponse>> {
    return apiGet<PageResponse<FavoriteItemResponse>>(`/favorites?page=${page}&size=${size}`);
  },
};
