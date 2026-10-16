import React, { useEffect, useState, useCallback } from "react";
import { Link } from "react-router-dom";
import { FavoriteItemResponse } from "../types/api";
import { favoriteService } from "../services/favoriteService";
import { useFavorite } from "../context/FavoriteContext";
import { formatPrice } from "../services/placeService";
import "./SavedPage.css";

export const SavedPage: React.FC = () => {
  const { toggleFavorite } = useFavorite();
  const [favorites, setFavorites] = useState<FavoriteItemResponse[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const loadFavorites = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const res = await favoriteService.listFavorites(0, 50);
      setFavorites(res.content);
    } catch {
      setError("Không thể tải danh sách địa điểm đã lưu.");
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    loadFavorites();
  }, [loadFavorites]);

  const handleRemove = async (e: React.MouseEvent, placeId: string, slug?: string) => {
    e.preventDefault();
    e.stopPropagation();

    // Optimistically remove from local state
    setFavorites((prev) => prev.filter((item) => item.place.id !== placeId));

    try {
      await toggleFavorite(placeId, slug);
    } catch {
      // If error occurs, reload list
      loadFavorites();
    }
  };

  return (
    <div className="vista-saved-page">
      <div className="dnvibe-container">
        {/* HEADER */}
        <div className="vista-saved-header">
          <div className="vista-saved-header__badge">
            <span className="material-symbols-outlined text-sm">favorite</span>
            <span>Bộ sưu tập cá nhân</span>
          </div>
          <h1 className="vista-saved-header__title">Địa điểm đã lưu</h1>
          <p className="vista-saved-header__subtitle">
            Các điểm đến bạn đã đánh dấu yêu thích để dễ dàng lên kế hoạch và trải nghiệm tại Đà Nẵng.
          </p>
        </div>

        {/* LOADING SKELETON */}
        {isLoading && (
          <div className="vista-saved-grid">
            {[1, 2, 3].map((n) => (
              <div key={n} className="vista-discovery-skeleton" style={{ height: "320px" }} />
            ))}
          </div>
        )}

        {/* ERROR STATE */}
        {!isLoading && error && (
          <div className="vista-saved-empty">
            <span className="material-symbols-outlined text-4xl" style={{ color: "#ef4444" }}>
              cloud_off
            </span>
            <p>{error}</p>
            <button
              type="button"
              onClick={loadFavorites}
              className="vista-auth-card__submit-btn"
              style={{ width: "auto", padding: "8px 24px" }}
            >
              Thử lại
            </button>
          </div>
        )}

        {/* EMPTY STATE */}
        {!isLoading && !error && favorites.length === 0 && (
          <div className="vista-saved-empty">
            <span
              className="material-symbols-outlined text-5xl"
              style={{ color: "var(--dnvibe-primary)", opacity: 0.8 }}
            >
              favorite_border
            </span>
            <h2 style={{ fontSize: "20px", fontWeight: 700, margin: 0, color: "var(--dnvibe-neutral-900)" }}>
              Bạn chưa lưu địa điểm nào
            </h2>
            <p style={{ fontSize: "14px", color: "var(--dnvibe-neutral-600)", margin: 0, lineHeight: 1.5 }}>
              Khám phá các danh lam thắng cảnh, bãi biển quyến rũ và khu ẩm thực đặc sắc tại Đà Nẵng và lưu lại những nơi bạn yêu thích.
            </p>
            <Link
              to="/places"
              className="vista-auth-card__submit-btn"
              style={{ textDecoration: "none", width: "auto", padding: "11px 28px", marginTop: "8px" }}
            >
              <span>Khám phá địa điểm ngay</span>
              <span className="material-symbols-outlined text-sm">arrow_forward</span>
            </Link>
          </div>
        )}

        {/* SAVED PLACES GRID */}
        {!isLoading && !error && favorites.length > 0 && (
          <div className="vista-saved-grid">
            {favorites.map((item) => {
              const place = item.place;
              if (!place) return null;
              const formattedPrice = formatPrice(Number(place.priceMin) || 0, Number(place.priceMax) || 0);

              return (
                <article key={item.favoriteId} className="vista-saved-card">
                  <Link
                    to={`/places/${place.slug || place.id}`}
                    style={{ textDecoration: "none", color: "inherit", display: "flex", flexDirection: "column", height: "100%" }}
                  >
                    <div className="vista-saved-card__thumb-wrap">
                      <img
                        src={place.thumbnailUrl || "/images/my-khe.png"}
                        alt={place.name}
                        className="vista-saved-card__thumb"
                        loading="lazy"
                      />
                      <button
                        type="button"
                        className="vista-saved-card__remove-btn"
                        onClick={(e) => handleRemove(e, place.id, place.slug)}
                        aria-label={`Bỏ lưu ${place.name}`}
                        title="Bỏ lưu khỏi danh sách"
                      >
                        <span className="material-symbols-outlined text-xl" style={{ fontVariationSettings: "'FILL' 1" }}>
                          favorite
                        </span>
                      </button>
                    </div>

                    <div className="vista-saved-card__content">
                      <div className="vista-saved-card__district">
                        <span className="material-symbols-outlined text-xs">location_on</span>
                        <span>{place.district} • {place.category}</span>
                      </div>

                      <h3 className="vista-saved-card__title">{place.name}</h3>

                      <div className="vista-saved-card__meta-row">
                        <div className="vista-saved-card__rating">
                          <span
                            className="material-symbols-outlined text-sm text-secondary"
                            style={{ fontVariationSettings: "'FILL' 1" }}
                          >
                            star
                          </span>
                          <span>{(Number(place.ratingAverage) || 4.5).toFixed(1)}</span>
                          <span style={{ fontSize: "12px", color: "var(--dnvibe-neutral-500)", fontWeight: 400 }}>
                            ({place.reviewCount || 0})
                          </span>
                        </div>

                        <div className="vista-saved-card__price">{formattedPrice}</div>
                      </div>
                    </div>
                  </Link>
                </article>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
};

export default SavedPage;
