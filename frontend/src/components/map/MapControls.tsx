import React from "react";
import "./MapPanel.css";

export interface MapControlsProps {
  mapType: "map" | "satellite";
  onMapTypeToggle: (type: "map" | "satellite") => void;
  onZoomIn: () => void;
  onZoomOut: () => void;
  onLocate: () => void;
  onToggleFullscreen?: () => void;
  onExploreAround?: () => void;
}

export const MapControls: React.FC<MapControlsProps> = ({
  mapType,
  onMapTypeToggle,
  onZoomIn,
  onZoomOut,
  onLocate,
  onToggleFullscreen,
  onExploreAround,
}) => {
  return (
    <>
      {/* TOP-RIGHT CONTROLS: STYLE TOGGLE & QUICK ACTIONS */}
      <div className="vista-map-controls__top-right">
        {/* Toggle pill: Bản đồ / Vệ tinh */}
        <div className="vista-map-type-toggle" role="group" aria-label="Kiểu hiển thị bản đồ">
          <button
            type="button"
            className={`vista-map-type-btn ${mapType === "map" ? "vista-map-type-btn--active" : ""}`}
            onClick={() => onMapTypeToggle("map")}
          >
            <span className="material-symbols-outlined text-[15px]">map</span>
            <span>Bản đồ</span>
          </button>
          <button
            type="button"
            className={`vista-map-type-btn ${mapType === "satellite" ? "vista-map-type-btn--active" : ""}`}
            onClick={() => onMapTypeToggle("satellite")}
          >
            <span className="material-symbols-outlined text-[15px]">satellite_alt</span>
            <span>Vệ tinh</span>
          </button>
        </div>

        {/* Quick action buttons */}
        <div className="vista-map-top-actions">
          <button
            type="button"
            className="vista-map-ctrl-btn vista-map-ctrl-btn--circle"
            onClick={onLocate}
            aria-label="Định vị vị trí hiện tại"
            title="Định vị hiện tại"
          >
            <span className="material-symbols-outlined text-[18px]">my_location</span>
          </button>

          {onToggleFullscreen && (
            <button
              type="button"
              className="vista-map-ctrl-btn vista-map-ctrl-btn--circle"
              onClick={onToggleFullscreen}
              aria-label="Toàn màn hình"
              title="Toàn màn hình"
            >
              <span className="material-symbols-outlined text-[18px]">fullscreen</span>
            </button>
          )}
        </div>
      </div>

      {/* BOTTOM-RIGHT: ZOOM & LOCATE CONTROLS */}
      <div className="vista-map-controls__bottom-right">
        <div className="vista-map-zoom-group">
          <button
            type="button"
            className="vista-map-ctrl-btn vista-map-zoom-btn"
            onClick={onZoomIn}
            aria-label="Phóng to bản đồ"
            title="Phóng to"
          >
            <span className="material-symbols-outlined text-[18px]">add</span>
          </button>
          <div className="vista-map-zoom-divider" />
          <button
            type="button"
            className="vista-map-ctrl-btn vista-map-zoom-btn"
            onClick={onZoomOut}
            aria-label="Thu nhỏ bản đồ"
            title="Thu nhỏ"
          >
            <span className="material-symbols-outlined text-[18px]">remove</span>
          </button>
        </div>

        <button
          type="button"
          className="vista-map-ctrl-btn vista-map-ctrl-btn--circle"
          onClick={onLocate}
          aria-label="Định vị của tôi"
          title="Tâm điểm Đà Nẵng"
        >
          <span className="material-symbols-outlined text-[18px]">near_me</span>
        </button>
      </div>

      {/* BOTTOM-LEFT: SCALE INDICATOR */}
      <div className="vista-map-controls__bottom-left">
        <div className="vista-map-scale" title="Thước tỉ lệ">
          <div className="vista-map-scale__bar" />
          <div className="vista-map-scale__labels">
            <span>0</span>
            <span>2</span>
            <span>5 km</span>
          </div>
        </div>
      </div>

      {/* BOTTOM AREA: EXPLORE AROUND FLOATING CARD */}
      <div className="vista-map-controls__bottom-center">
        <div
          className="vista-map-explore-chip"
          role="button"
          tabIndex={0}
          onClick={onExploreAround}
          onKeyDown={(e) => {
            if (e.key === "Enter" || e.key === " ") {
              e.preventDefault();
              if (onExploreAround) onExploreAround();
            }
          }}
          title="Khám phá thêm địa điểm quanh đây"
          aria-label="Khám phá thêm địa điểm quanh đây"
        >
          <img
            src="/images/cau-rong.png"
            alt="Địa điểm quanh đây"
            className="vista-map-explore-chip__img"
          />
          <div className="vista-map-explore-chip__text">
            <span>Khám phá thêm</span>
            <strong>địa điểm quanh đây</strong>
          </div>
          <span className="material-symbols-outlined vista-map-explore-chip__chevron">
            chevron_right
          </span>
        </div>
      </div>
    </>
  );
};

export default MapControls;
