import React from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { DiscoverPlaceItem } from "../../data/mockPlaces";
import { useFavorite } from "../../context/FavoriteContext";
import { useAuth } from "../../context/AuthContext";
import "./DiscoverPlaceCard.css";

export interface DiscoverPlaceCardProps {
  place: DiscoverPlaceItem;
  isSelected?: boolean;
  onSelect?: (id: string) => void;
  onFavoriteToggle?: (id: string, isFav: boolean) => void;
}

export const DiscoverPlaceCard: React.FC<DiscoverPlaceCardProps> = ({
  place,
  isSelected = false,
  onSelect,
  onFavoriteToggle,
}) => {
  const { isAuthenticated } = useAuth();
  const { isFavorited, toggleFavorite } = useFavorite();
  const navigate = useNavigate();
  const location = useLocation();

  const isFavorite = isFavorited(place.id) || Boolean(place.isFavorite);

  const handleHeartClick = async (e: React.MouseEvent) => {
    e.stopPropagation();

    if (!isAuthenticated) {
      navigate("/login", { state: { from: location } });
      return;
    }

    try {
      const nextState = await toggleFavorite(place.id);
      if (onFavoriteToggle) {
        onFavoriteToggle(place.id, nextState);
      }
    } catch {
      // Handled by context rollback
    }
  };

  const handleCardClick = () => {
    if (onSelect) {
      onSelect(place.id);
    }
  };

  return (
    <article
      className={`vista-discover-card ${isSelected ? "vista-discover-card--selected" : ""}`}
      onClick={handleCardClick}
      role="button"
      tabIndex={0}
      onKeyDown={(e) => {
        if (e.key === "Enter" || e.key === " ") {
          e.preventDefault();
          handleCardClick();
        }
      }}
      aria-pressed={isSelected}
    >
      {/* LEFT: THUMBNAIL */}
      <div className="vista-discover-card__thumb-wrap">
        <img
          src={place.imageUrl}
          alt={place.name}
          className="vista-discover-card__thumb"
          loading="lazy"
        />

        {place.isFeatured && (
          <div className="vista-discover-card__featured-badge">
            <span
              className="material-symbols-outlined text-xs"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              star
            </span>
            <span>Nổi bật</span>
          </div>
        )}
      </div>

      {/* RIGHT: DETAILS */}
      <div className="vista-discover-card__info">
        {/* TOP ROW: TITLE & FAVORITE */}
        <div className="vista-discover-card__top">
          <Link
            to={`/places/${place.id}`}
            className="vista-discover-card__name"
            style={{ textDecoration: "none", color: "inherit" }}
            onClick={(e) => e.stopPropagation()}
            title="Xem chi tiết địa điểm"
          >
            {place.name}
          </Link>

          <button
            type="button"
            className={`vista-discover-card__fav-btn ${isFavorite ? "vista-discover-card__fav-btn--active" : ""}`}
            onClick={handleHeartClick}
            aria-label={isFavorite ? "Bỏ lưu địa điểm" : "Lưu vào danh sách yêu thích"}
            title={isFavorite ? "Đã lưu" : "Lưu địa điểm"}
          >
            <span
              className="material-symbols-outlined text-lg"
              style={isFavorite ? { fontVariationSettings: "'FILL' 1" } : undefined}
            >
              favorite
            </span>
          </button>
        </div>

        {/* DISTRICT & RATING ROW */}
        <div className="vista-discover-card__meta-row">
          <div className="vista-discover-card__district">
            <span className="material-symbols-outlined text-sm text-primary">location_on</span>
            <span>{place.district}</span>
          </div>

          <div className="vista-discover-card__rating">
            <span
              className="material-symbols-outlined text-sm vista-discover-card__star"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              star
            </span>
            <span className="vista-discover-card__rating-score">{place.rating.toFixed(1)}</span>
            <span className="vista-discover-card__rating-count">({place.reviewCount})</span>
          </div>
        </div>

        {/* TAG CHIPS */}
        <div className="vista-discover-card__tags">
          {place.tags.map((tag) => (
            <span key={tag} className="vista-discover-card__tag">
              {tag}
            </span>
          ))}
        </div>

        {/* BOTTOM METADATA: PRICE, BEST TIME, CROWD */}
        <div className="vista-discover-card__bottom">
          <div className="vista-discover-card__spec">
            <span className="material-symbols-outlined text-sm text-primary">sell</span>
            <span>{place.price}</span>
          </div>

          <div className="vista-discover-card__spec">
            <span className="material-symbols-outlined text-sm text-secondary">wb_sunny</span>
            <span>{place.bestTime}</span>
          </div>

          <div className="vista-discover-card__spec">
            <span className="material-symbols-outlined text-sm text-primary">group</span>
            <span>{place.crowdLevel}</span>
          </div>
        </div>
      </div>
    </article>
  );
};

export default DiscoverPlaceCard;
