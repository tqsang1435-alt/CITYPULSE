import React from "react";
import "./MapPanel.css";

export interface MapPinProps {
  id: string;
  name: string;
  x: number; // percentage (0 - 100)
  y: number; // percentage (0 - 100)
  isSelected?: boolean;
  rating?: number;
  icon?: string;
  onClick?: () => void;
}

export const MapPin: React.FC<MapPinProps> = ({
  name,
  x,
  y,
  isSelected = false,
  rating,
  icon,
  onClick,
}) => {
  return (
    <div
      className={`vista-map-pin ${isSelected ? "vista-map-pin--selected" : ""}`}
      style={{ left: `${x}%`, top: `${y}%` }}
      onClick={onClick}
      role="button"
      tabIndex={0}
      title={name}
      aria-label={`Địa điểm: ${name}${rating ? `, đánh giá ${rating} sao` : ""}`}
      onKeyDown={(e) => {
        if (e.key === "Enter" || e.key === " ") {
          e.preventDefault();
          if (onClick) onClick();
        }
      }}
    >
      {/* PULSING TEAL AURA FOR SELECTED PIN */}
      {isSelected && (
        <span className="vista-map-pin__pulse-ring" aria-hidden="true" />
      )}

      {/* PILL BADGE */}
      <div className="vista-map-pin__badge">
        {icon && (
          <span
            className="material-symbols-outlined vista-map-pin__icon"
            style={{ fontVariationSettings: "'FILL' 1" }}
          >
            {icon}
          </span>
        )}
        <span className="vista-map-pin__name">{name}</span>
        {rating && (
          <span className="vista-map-pin__rating">
            {rating.toFixed(1)}★
          </span>
        )}
      </div>

      {/* DOWNWARD ARROW NIB */}
      <div className="vista-map-pin__nib" aria-hidden="true" />
    </div>
  );
};

export default MapPin;
