import React, { useState } from "react";
import MapPin from "./MapPin";
import MapPlacePopup from "./MapPlacePopup";
import MapControls from "./MapControls";
import { DiscoverPlaceItem, MAP_LANDMARKS } from "../../data/mockPlaces";
import "./MapPanel.css";

export interface MapPanelProps {
  places: DiscoverPlaceItem[];
  selectedId: string;
  onSelectPlace: (id: string) => void;
}

export const MapPanel: React.FC<MapPanelProps> = ({
  places,
  selectedId,
  onSelectPlace,
}) => {
  const [mapType, setMapType] = useState<"map" | "satellite">("map");
  const [zoomLevel, setZoomLevel] = useState(1);
  const [showPopup, setShowPopup] = useState(true);
  const [isFullscreen, setIsFullscreen] = useState(false);

  const selectedPlace = places.find((p) => p.id === selectedId) || places[0];

  const handleZoomIn = () => {
    setZoomLevel((prev) => Math.min(Number((prev + 0.15).toFixed(2)), 1.5));
  };

  const handleZoomOut = () => {
    setZoomLevel((prev) => Math.max(Number((prev - 0.15).toFixed(2)), 0.85));
  };

  const handleLocate = () => {
    setZoomLevel(1);
    onSelectPlace("my-khe-beach");
    setShowPopup(true);
  };

  const handleToggleFullscreen = () => {
    setIsFullscreen((prev) => !prev);
  };

  const handleExploreAround = () => {
    // Cycle to next place or Cầu Rồng / Chợ Hàn
    const currentIndex = places.findIndex((p) => p.id === selectedId);
    const nextIndex = (currentIndex + 1) % places.length;
    onSelectPlace(places[nextIndex].id);
    setShowPopup(true);
  };

  return (
    <div
      className={`vista-map-panel ${
        mapType === "satellite" ? "vista-map-panel--satellite" : ""
      } ${isFullscreen ? "vista-map-panel--fullscreen" : ""}`}
    >
      {/* MAP CANVAS VIEWPORT */}
      <div
        className="vista-map-viewport"
        style={{
          transform: `scale(${zoomLevel})`,
          transformOrigin: "60% 50%",
        }}
      >
        {/* HIGH-FIDELITY DA NANG COASTAL SVG ARTWORK */}
        <svg
          viewBox="0 0 800 700"
          className="vista-map-svg"
          preserveAspectRatio="xMidYMid slice"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <defs>
            {/* OCEAN GRADIENTS */}
            <linearGradient id="oceanGrad" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stopColor="#CBEFF2" />
              <stop offset="50%" stopColor="#B2E7EC" />
              <stop offset="100%" stopColor="#9ADFE6" />
            </linearGradient>

            <linearGradient id="satOceanGrad" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stopColor="#0B2633" />
              <stop offset="60%" stopColor="#081E29" />
              <stop offset="100%" stopColor="#04121A" />
            </linearGradient>

            {/* GOLDEN SAND GRADIENT */}
            <linearGradient id="sandGrad" x1="0%" y1="0%" x2="100%" y2="0%">
              <stop offset="0%" stopColor="#FDE8C7" />
              <stop offset="60%" stopColor="#F6DEC0" />
              <stop offset="100%" stopColor="#F2D1A6" />
            </linearGradient>

            {/* RIVER GRADIENTS */}
            <linearGradient id="riverGrad" x1="0%" y1="0%" x2="0%" y2="100%">
              <stop offset="0%" stopColor="#56D9D7" stopOpacity="0.8" />
              <stop offset="50%" stopColor="#2BB8B5" stopOpacity="0.9" />
              <stop offset="100%" stopColor="#159C9A" stopOpacity="0.95" />
            </linearGradient>

            <linearGradient id="satRiverGrad" x1="0%" y1="0%" x2="0%" y2="100%">
              <stop offset="0%" stopColor="#0891B2" stopOpacity="0.85" />
              <stop offset="100%" stopColor="#0E7490" stopOpacity="0.95" />
            </linearGradient>

            {/* LAND GRADIENTS */}
            <linearGradient id="landGrad" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stopColor="#F8FCFC" />
              <stop offset="50%" stopColor="#F0F8F7" />
              <stop offset="100%" stopColor="#E9F5F4" />
            </linearGradient>

            <linearGradient id="satLandGrad" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stopColor="#1A2F25" />
              <stop offset="100%" stopColor="#12221A" />
            </linearGradient>

            {/* MOUNTAIN SHADING */}
            <linearGradient id="mountainGrad" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stopColor="#D2EFE7" />
              <stop offset="100%" stopColor="#BFECDFA" />
            </linearGradient>
          </defs>

          {/* BASE LAND BACKGROUND LAYER */}
          <rect
            width="800"
            height="700"
            fill={mapType === "satellite" ? "url(#satLandGrad)" : "url(#landGrad)"}
          />

          {/* WESTERN TOPOGRAPHIC MOUNTAINS (BA NA HILLS & TRUONG SON SPUR) */}
          <g className="vista-map-western-mountains" opacity={mapType === "satellite" ? "0.35" : "0.75"}>
            <path
              d="M -30 110 C 50 130, 90 180, 120 240 C 145 295, 115 375, 75 440 C 35 505, -30 550, -30 550 Z"
              fill={mapType === "satellite" ? "#16281F" : "#DCF0E9"}
            />
            <path
              d="M -30 170 C 30 185, 65 225, 85 270 C 95 315, 70 370, 35 415 C 5 455, -30 480, -30 480 Z"
              fill={mapType === "satellite" ? "#112018" : "#CBEAE0"}
              stroke={mapType === "satellite" ? "#1F3D2C" : "#8EC8B8"}
              strokeWidth="1.2"
              strokeDasharray="6 4"
            />
            <path
              d="M -30 230 C 10 240, 35 270, 50 305 C 55 335, 35 375, 10 405 C -10 430, -30 440, -30 440 Z"
              fill={mapType === "satellite" ? "#0C1711" : "#B8E2D5"}
              opacity="0.8"
            />
            {/* Cable car line leading to Ba Na */}
            <path
              d="M 60 270 C 120 280, 170 295, 220 310"
              fill="none"
              stroke={mapType === "satellite" ? "#38BDF8" : "#006A68"}
              strokeWidth="1.8"
              strokeDasharray="4 4"
              opacity="0.65"
            />
          </g>

          {/* DAD AIRPORT RUNWAYS (WEST-CENTRAL) */}
          <g
            transform="translate(205, 365) rotate(-22)"
            opacity={mapType === "satellite" ? "0.6" : "0.85"}
          >
            <rect x="0" y="0" width="7" height="52" rx="2" fill={mapType === "satellite" ? "#64748B" : "#CBD5E1"} />
            <rect x="13" y="8" width="7" height="42" rx="2" fill={mapType === "satellite" ? "#64748B" : "#CBD5E1"} />
            {/* Runway dash markings */}
            <line x1="3.5" y1="4" x2="3.5" y2="48" stroke="#FFFFFF" strokeWidth="1" strokeDasharray="3 3" opacity="0.6" />
          </g>

          {/* URBAN STREET NETWORK (CLEAN ARCHITECTURAL ARTERIALS) */}
          <g opacity={mapType === "satellite" ? "0.3" : "0.7"}>
            {/* East-West Grid Dashes */}
            <path
              d="M 50 180 L 480 200 M 40 280 L 460 300 M 60 400 L 480 430 M 70 540 L 520 560"
              stroke={mapType === "satellite" ? "#2A4737" : "#CCE5E5"}
              strokeDasharray="6 6"
              strokeWidth="1.6"
            />
            {/* North-South Grid Dashes */}
            <path
              d="M 180 50 L 190 650 M 260 40 L 270 660 M 390 50 L 400 650 M 450 60 L 470 660"
              stroke={mapType === "satellite" ? "#2A4737" : "#CCE5E5"}
              strokeDasharray="6 6"
              strokeWidth="1.6"
            />
          </g>

          {/* ARTERIAL BOULEVARDS */}
          {/* Dien Bien Phu - Nguyen Tat Thanh Axis */}
          <path
            d="M 330 200 C 260 215, 170 230, 70 210"
            fill="none"
            stroke="#FFFFFF"
            strokeWidth="4"
            opacity={mapType === "satellite" ? "0.4" : "0.95"}
          />
          <path
            d="M 330 200 C 260 215, 170 230, 70 210"
            fill="none"
            stroke={mapType === "satellite" ? "#D97706" : "#FDE8C7"}
            strokeWidth="2.4"
            opacity="0.8"
          />

          {/* Nguyen Van Linh - Vo Van Kiet Axis (Dragon Bridge Corridor to Beach) */}
          <path
            d="M 220 320 L 490 320"
            fill="none"
            stroke="#FFFFFF"
            strokeWidth="5"
            opacity={mapType === "satellite" ? "0.5" : "0.95"}
          />
          <path
            d="M 220 320 L 490 320"
            fill="none"
            stroke={mapType === "satellite" ? "#F59E0B" : "#FDE8C7"}
            strokeWidth="3"
            opacity="0.85"
          />

          {/* HAN RIVER FLOWING NORTH-SOUTH THROUGH CITY */}
          <path
            d="M 330 0 C 340 100, 310 220, 320 340 C 330 460, 360 580, 340 700"
            fill="none"
            opacity="0.88"
            stroke={mapType === "satellite" ? "url(#satRiverGrad)" : "url(#riverGrad)"}
            strokeLinecap="round"
            strokeWidth="36"
          />
          {/* Inner River Highlight Flow */}
          <path
            d="M 330 0 C 340 100, 310 220, 320 340 C 330 460, 360 580, 340 700"
            fill="none"
            opacity="0.45"
            stroke={mapType === "satellite" ? "#38BDF8" : "#80F6F3"}
            strokeLinecap="round"
            strokeWidth="18"
          />

          {/* BRIDGES OVER HAN RIVER */}
          {/* 1. Thuan Phuoc Bridge (Northern River Mouth) */}
          <g transform="translate(316, 142) rotate(-8)">
            <rect
              x="0"
              y="0"
              width="36"
              height="6"
              rx="2"
              fill={mapType === "satellite" ? "#38BDF8" : "#006A68"}
            />
          </g>

          {/* 2. Han River Swing Bridge */}
          <g transform="translate(305, 230)">
            <rect
              x="0"
              y="0"
              width="32"
              height="5"
              rx="1.5"
              fill={mapType === "satellite" ? "#60A5FA" : "#14B8A6"}
              opacity="0.85"
            />
          </g>

          {/* 3. Iconic Dragon Bridge Symbol (Cau Rong Golden Notch) */}
          <path
            d="M 292 320 Q 328 312 356 320"
            stroke="#FEBE46"
            strokeWidth="8"
            strokeLinecap="round"
          />

          {/* 4. Tran Thi Ly Bridge */}
          <line
            x1="305"
            y1="400"
            x2="345"
            y2="400"
            stroke="#F87171"
            strokeWidth="4.5"
            strokeLinecap="round"
            opacity="0.85"
          />

          {/* OCEAN BODY (EAST / RIGHT SIDE) */}
          <path
            d="M 520 0 C 490 120, 500 240, 480 380 C 460 500, 520 620, 540 700 L 800 700 L 800 0 Z"
            fill={mapType === "satellite" ? "url(#satOceanGrad)" : "url(#oceanGrad)"}
          />

          {/* GOLDEN SAND COASTAL STRIP (MY KHE / NON NUOC BEACH) */}
          <path
            d="M 515 0 C 485 120, 495 240, 475 380 C 455 500, 515 620, 535 700 L 548 700 C 528 620, 468 500, 488 380 C 508 240, 498 120, 528 0 Z"
            fill={mapType === "satellite" ? "#4D5940" : "url(#sandGrad)"}
          />

          {/* COASTAL BOULEVARD (VO NGUYEN GIAP / HOANG SA / TRUONG SA) */}
          <path
            d="M 512 0 C 482 120, 492 240, 472 380 C 452 500, 512 620, 532 700"
            fill="none"
            stroke="#FFFFFF"
            strokeWidth="4"
            opacity={mapType === "satellite" ? "0.3" : "0.9"}
          />
          <path
            d="M 512 0 C 482 120, 492 240, 472 380 C 452 500, 512 620, 532 700"
            fill="none"
            stroke={mapType === "satellite" ? "#F59E0B" : "#FDE8C7"}
            strokeWidth="2.2"
            opacity="0.85"
          />

          {/* SON TRA PENINSULA MOUNTAIN HEADLAND (TOP RIGHT) */}
          <path
            d="M 450 0 C 480 30, 550 20, 640 40 C 720 60, 760 120, 780 160 C 730 200, 620 180, 560 150 C 500 120, 470 60, 450 0 Z"
            fill={mapType === "satellite" ? "#1B3A2C" : "#C5E5DC"}
            stroke={mapType === "satellite" ? "#2B5542" : "#9ED1C5"}
            strokeWidth="1.5"
          />
          {/* Son Tra Contours & Elevation */}
          <path
            d="M 520 50 C 570 40, 640 60, 710 90 C 740 120, 720 150, 670 155 C 600 150, 540 110, 520 50 Z"
            fill={mapType === "satellite" ? "#152C21" : "#B0DC CF"}
            opacity={mapType === "satellite" ? "0.5" : "0.7"}
          />
          <path
            d="M 580 70 C 620 65, 670 80, 690 110 C 670 130, 620 125, 580 70 Z"
            fill={mapType === "satellite" ? "#0F2018" : "#98CFBE"}
            opacity="0.6"
          />

          {/* NGU HANH SON (MARBLE MOUNTAINS - SOUTHEAST) */}
          <g
            transform="translate(565, 520)"
            opacity={mapType === "satellite" ? "0.45" : "0.85"}
          >
            <path
              d="M 10 35 C 20 15, 35 15, 45 35 C 55 20, 70 20, 80 40 C 85 55, 65 65, 40 62 C 20 60, 5 50, 10 35 Z"
              fill={mapType === "satellite" ? "#1E3B2E" : "#C5E5DC"}
              stroke={mapType === "satellite" ? "#2F5844" : "#9ED1C5"}
              strokeWidth="1.2"
            />
          </g>

          {/* COASTAL OCEAN WAVES SHIMMER (SUBTLE & RELAXING) */}
          <g opacity={mapType === "satellite" ? "0.15" : "0.6"}>
            <path
              d="M 580 200 Q 610 190 640 200 T 700 200"
              stroke="#FFFFFF"
              strokeWidth="2"
              strokeLinecap="round"
            />
            <path
              d="M 620 360 Q 650 350 680 360 T 740 360"
              stroke="#FFFFFF"
              strokeWidth="2"
              strokeLinecap="round"
            />
            <path
              d="M 570 520 Q 600 510 630 520 T 690 520"
              stroke="#FFFFFF"
              strokeWidth="2"
              strokeLinecap="round"
            />
            <path
              d="M 640 620 Q 670 610 700 620 T 750 620"
              stroke="#FFFFFF"
              strokeWidth="1.8"
              strokeLinecap="round"
            />
          </g>

          {/* SUBTLE COASTAL SAILBOATS OFF MY KHE */}
          <g
            transform="translate(680, 260) scale(0.9)"
            opacity={mapType === "satellite" ? "0.3" : "0.85"}
          >
            <path d="M 0 10 L 12 10 L 9 14 L 3 14 Z" fill="#7D5700" />
            <path d="M 6 0 L 6 10 L 12 10 Z" fill="#FFFFFF" />
          </g>
          <g
            transform="translate(710, 440) scale(0.75)"
            opacity={mapType === "satellite" ? "0.3" : "0.75"}
          >
            <path d="M 0 10 L 12 10 L 9 14 L 3 14 Z" fill="#7D5700" />
            <path d="M 6 0 L 6 10 L 12 10 Z" fill="#FFFFFF" />
          </g>

          {/* SUBTLE WATERMARK "Biển Mỹ Khê" */}
          <text
            x="675"
            y="390"
            fill={mapType === "satellite" ? "#38BDF8" : "#006A68"}
            opacity={mapType === "satellite" ? "0.2" : "0.3"}
            fontFamily="Plus Jakarta Sans, sans-serif"
            fontSize="14"
            fontStyle="italic"
            fontWeight="700"
            letterSpacing="0.05em"
          >
            Biển Mỹ Khê
          </text>
        </svg>

        {/* MAP LANDMARK PILLS */}
        {MAP_LANDMARKS.map((lm) => (
          <div
            key={lm.id}
            className={`vista-map-landmark vista-map-landmark--${lm.iconType}`}
            style={{ left: `${lm.x}%`, top: `${lm.y}%` }}
          >
            <div className="vista-map-landmark__badge">
              {lm.iconType === "airport" && (
                <span className="material-symbols-outlined vista-map-landmark__icon">
                  flight
                </span>
              )}
              {lm.iconType === "cablecar" && (
                <span className="material-symbols-outlined vista-map-landmark__icon">
                  tram
                </span>
              )}
              {lm.iconType === "mountain" && (
                <span className="material-symbols-outlined vista-map-landmark__icon">
                  filter_hdr
                </span>
              )}
              {lm.iconType === "forest" && (
                <span className="material-symbols-outlined vista-map-landmark__icon">
                  forest
                </span>
              )}
              {lm.iconType === "bridge" && (
                <span className="material-symbols-outlined vista-map-landmark__icon">
                  commit
                </span>
              )}
              <span className="vista-map-landmark__name">{lm.name}</span>
            </div>
          </div>
        ))}

        {/* DISCOVER PLACES INTERACTIVE PINS */}
        {places.map((place) => (
          <MapPin
            key={place.id}
            id={place.id}
            name={place.name}
            x={place.mapCoords.x}
            y={place.mapCoords.y}
            rating={place.rating}
            icon={place.iconName}
            isSelected={place.id === selectedId}
            onClick={() => {
              onSelectPlace(place.id);
              setShowPopup(true);
            }}
          />
        ))}

        {/* SELECTED DESTINATION POPUP */}
        {showPopup && selectedPlace && (
          <MapPlacePopup
            place={selectedPlace}
            onClose={() => setShowPopup(false)}
          />
        )}
      </div>

      {/* MAP CONTROLS OVERLAY */}
      <MapControls
        mapType={mapType}
        onMapTypeToggle={(type) => setMapType(type)}
        onZoomIn={handleZoomIn}
        onZoomOut={handleZoomOut}
        onLocate={handleLocate}
        onToggleFullscreen={handleToggleFullscreen}
        onExploreAround={handleExploreAround}
      />
    </div>
  );
};

export default MapPanel;
