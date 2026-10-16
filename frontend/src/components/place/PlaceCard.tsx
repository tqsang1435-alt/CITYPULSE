import React from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { PlaceItem } from "../../data/mockPlaces";
import { useFavorite } from "../../context/FavoriteContext";
import { useAuth } from "../../context/AuthContext";
import "./PlaceCard.css";

export interface PlaceCardProps {
  place: PlaceItem;
  onFavoriteToggle?: (id: string, isFav: boolean) => void;
}

export const PlaceCard: React.FC<PlaceCardProps> = ({ place, onFavoriteToggle }) => {
  const { isAuthenticated } = useAuth();
  const { isFavorited, toggleFavorite } = useFavorite();
  const navigate = useNavigate();
  const location = useLocation();

  const isFavorite = isFavorited(place.id) || Boolean(place.isFavorite);

  const handleHeartClick = async (e: React.MouseEvent) => {
    e.preventDefault();
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
      // Failed toggle handled by context rollback
    }
  };

  const getCrowdBadgeClass = (type: PlaceItem["crowdLevelType"]) => {
    switch (type) {
      case "ideal":
        return "vista-place-card__crowd--ideal";
      case "crowded":
        return "vista-place-card__crowd--crowded";
      case "moderate":
      default:
        return "vista-place-card__crowd--moderate";
    }
  };

  return (
    <article className="vista-place-card">
      <Link to={`/places/${place.id}`} className="vista-place-card__link">
        {/* MEDIA CONTAINER */}
        <div className="vista-place-card__media">
          <img
            src={place.imageUrl}
            alt={place.name}
            className="vista-place-card__image"
            loading="lazy"
          />

          {/* TOP FLOATING CONTROLS */}
          <div className="vista-place-card__top-overlay">
            {/* RATING BADGE */}
            <div className="vista-place-card__rating-pill">
              <span
                className="material-symbols-outlined vista-place-card__star-icon"
                style={{ fontVariationSettings: "'FILL' 1" }}
              >
                star
              </span>
              <span className="vista-place-card__rating-val">{place.rating.toFixed(1)}</span>
              <span className="vista-place-card__review-count">({place.reviewCount})</span>
            </div>

            {/* FAVORITE BUTTON */}
            <button
              type="button"
              className={`vista-place-card__fav-btn ${isFavorite ? "vista-place-card__fav-btn--active" : ""}`}
              onClick={handleHeartClick}
              aria-label={isFavorite ? "Bỏ lưu địa điểm" : "Lưu vào danh sách yêu thích"}
              title={isFavorite ? "Đã lưu vào danh sách" : "Lưu địa điểm này"}
            >
              <span
                className="material-symbols-outlined"
                style={isFavorite ? { fontVariationSettings: "'FILL' 1" } : undefined}
              >
                favorite
              </span>
            </button>
          </div>

          {/* CROWD LEVEL BADGE */}
          <div className="vista-place-card__bottom-overlay">
            <div className={`vista-place-card__crowd-pill ${getCrowdBadgeClass(place.crowdLevelType)}`}>
              <span className="vista-place-card__crowd-dot" aria-hidden="true" />
              <span>Mật độ: {place.crowdLevel}</span>
            </div>
          </div>
        </div>

        {/* CARD BODY CONTENT */}
        <div className="vista-place-card__content">
          <div>
            <div className="vista-place-card__district-tag">
              <span className="material-symbols-outlined text-xs">place</span>
              <span>{place.district} • {place.categoryTag}</span>
            </div>

            <h3 className="vista-place-card__title">{place.name}</h3>
            <p className="vista-place-card__desc">{place.description}</p>
          </div>

          {/* METADATA BADGES */}
          <div className="vista-place-card__meta">
            <div className="vista-place-card__meta-row">
              <span className="vista-place-card__meta-label">
                <span className="material-symbols-outlined text-primary text-sm">schedule</span>
                Thời điểm đẹp nhất
              </span>
              <span className="vista-place-card__meta-badge vista-place-card__meta-badge--time">
                {place.bestTime}
              </span>
            </div>

            <div className="vista-place-card__meta-row">
              <span className="vista-place-card__meta-label">
                <span className="material-symbols-outlined text-secondary text-sm">monetization_on</span>
                Chi phí dự kiến
              </span>
              <span className="vista-place-card__meta-badge vista-place-card__meta-badge--price">
                {place.price}
              </span>
            </div>
          </div>
        </div>
      </Link>
    </article>
  );
};

export default PlaceCard;
