import React, { createContext, useContext, useEffect, useState, useCallback } from "react";
import { favoriteService } from "../services/favoriteService";
import { useAuth } from "./AuthContext";

export interface FavoriteContextType {
  favoriteIds: Set<string>;
  isFavorited: (placeIdOrSlug: string) => boolean;
  toggleFavorite: (placeId: string, slug?: string) => Promise<boolean>;
  refreshFavorites: () => Promise<void>;
  isLoadingFavorites: boolean;
}

const FavoriteContext = createContext<FavoriteContextType | undefined>(undefined);

export const FavoriteProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated } = useAuth();
  const [favoriteIds, setFavoriteIds] = useState<Set<string>>(new Set());
  const [isLoadingFavorites, setIsLoadingFavorites] = useState<boolean>(false);

  const refreshFavorites = useCallback(async () => {
    if (!isAuthenticated) {
      setFavoriteIds(new Set());
      return;
    }

    try {
      setIsLoadingFavorites(true);
      const res = await favoriteService.listFavorites(0, 100);
      const ids = new Set<string>();
      res.content.forEach((item) => {
        if (item.place) {
          if (item.place.id) ids.add(item.place.id);
          if (item.place.slug) ids.add(item.place.slug);
        }
      });
      setFavoriteIds(ids);
    } catch {
      // Ignore or log error
    } finally {
      setIsLoadingFavorites(false);
    }
  }, [isAuthenticated]);

  useEffect(() => {
    if (isAuthenticated) {
      refreshFavorites();
    } else {
      setFavoriteIds(new Set());
    }
  }, [isAuthenticated, refreshFavorites]);

  const isFavorited = useCallback(
    (placeIdOrSlug: string): boolean => {
      if (!placeIdOrSlug) return false;
      return favoriteIds.has(placeIdOrSlug);
    },
    [favoriteIds]
  );

  const toggleFavorite = useCallback(
    async (placeId: string, slug?: string): Promise<boolean> => {
      if (!isAuthenticated) {
        return false;
      }

      const currentlyFav = isFavorited(placeId) || (slug ? isFavorited(slug) : false);
      const nextFav = !currentlyFav;

      // Optimistic update
      setFavoriteIds((prev) => {
        const next = new Set(prev);
        if (nextFav) {
          next.add(placeId);
          if (slug) next.add(slug);
        } else {
          next.delete(placeId);
          if (slug) next.delete(slug);
        }
        return next;
      });

      try {
        if (nextFav) {
          await favoriteService.addFavorite(placeId);
        } else {
          await favoriteService.removeFavorite(placeId);
        }
        return nextFav;
      } catch (err) {
        // Rollback optimistic state on error
        setFavoriteIds((prev) => {
          const rollback = new Set(prev);
          if (currentlyFav) {
            rollback.add(placeId);
            if (slug) rollback.add(slug);
          } else {
            rollback.delete(placeId);
            if (slug) rollback.delete(slug);
          }
          return rollback;
        });
        throw err;
      }
    },
    [isAuthenticated, isFavorited]
  );

  return (
    <FavoriteContext.Provider
      value={{
        favoriteIds,
        isFavorited,
        toggleFavorite,
        refreshFavorites,
        isLoadingFavorites,
      }}
    >
      {children}
    </FavoriteContext.Provider>
  );
};

export function useFavorite(): FavoriteContextType {
  const context = useContext(FavoriteContext);
  if (!context) {
    throw new Error("useFavorite must be used within a FavoriteProvider");
  }
  return context;
}
