import React from "react";
import { Link } from "react-router-dom";
import { DiscoverPlaceItem } from "../../data/mockPlaces";
import "./MapPanel.css";

export interface MapPlacePopupProps {
  place: DiscoverPlaceItem;
  onClose?: () => void;
}

export const MapPlacePopup: React.FC<MapPlacePopupProps> = ({ place, onClose }) => {
  // If selected place is Mỹ Khê Beach: position elegantly below the pin pointing up to it
  const isMyKhe = place.id === "my-khe-beach";
  const popupLeft = isMyKhe ? 70 : place.mapCoords.x;
  const popupTop = isMyKhe ? 48 : place.mapCoords.y;
  
  let positionClass = "vista-map-popup--below-pin";
  if (!isMyKhe) {
    if (place.mapCoords.x < 30) {
      positionClass = "vista-map-popup--right";
    } else if (place.mapCoords.y > 65) {
      positionClass = "vista-map-popup--above";
    } else if (place.mapCoords.x > 60) {
      positionClass = "vista-map-popup--left";
    } else {
      positionClass = "vista-map-popup--below-pin";
    }
  }

  return (
    <div
      className={`vista-map-popup ${positionClass}`}
      style={{ left: `${popupLeft}%`, top: `${popupTop}%` }}
    >
      <div className="vista-map-popup__inner">
        {/* THUMBNAIL WITH DISTANCE BADGE */}
        <div className="vista-map-popup__thumb-wrap">
          <img
            src={place.imageUrl}
            alt={place.name}
            className="vista-map-popup__thumb"
          />
          <span className="vista-map-popup__dist-badge">1.2 km</span>
        </div>

        {/* DETAILS */}
        <div className="vista-map-popup__content">
          <div className="vista-map-popup__header">
            <h4 className="vista-map-popup__title">{place.name}</h4>
            {onClose && (
              <button
                type="button"
                className="vista-map-popup__close-btn"
                onClick={onClose}
                aria-label="Đóng popup"
              >
                ✕
              </button>
            )}
          </div>

          <div className="vista-map-popup__meta">
            <div className="vista-map-popup__rating">
              <span
                className="material-symbols-outlined text-xs text-secondary"
                style={{ fontVariationSettings: "'FILL' 1" }}
              >
                star
              </span>
              <span className="vista-map-popup__score">{place.rating.toFixed(1)}</span>
              <span className="vista-map-popup__count">({place.reviewCount})</span>
            </div>
            <span className="vista-map-popup__separator">•</span>
            <span className="vista-map-popup__district">{place.district}</span>
          </div>

          {/* TAGS */}
          <div className="vista-map-popup__tags">
            {place.tags.slice(0, 2).map((tag) => (
              <span key={tag} className="vista-map-popup__tag">
                {tag}
              </span>
            ))}
          </div>
        </div>

        {/* CIRCULAR ARROW CTA LINK */}
        <Link
          to={`/places?id=${place.id}`}
          className="vista-map-popup__cta-btn"
          title={`Xem chi tiết ${place.name}`}
          aria-label={`Xem chi tiết ${place.name}`}
        >
          <span className="material-symbols-outlined text-base">arrow_forward</span>
        </Link>
      </div>

      {/* POINTER ARROW NIB */}
      <div className="vista-map-popup__pointer-nib" aria-hidden="true" />
    </div>
  );
};

export default MapPlacePopup;
