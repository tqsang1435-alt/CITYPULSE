import React, { useState } from "react";
import DiscoverHeader from "../components/discover/DiscoverHeader";
import DiscoverFilterBar, { FilterState } from "../components/discover/DiscoverFilterBar";
import DiscoverPlaceList from "../components/discover/DiscoverPlaceList";
import MapPanel from "../components/map/MapPanel";
import { MOCK_DISCOVER_PLACES, DiscoverPlaceItem } from "../data/mockPlaces";
import "./DiscoverPage.css";

export const DiscoverPage: React.FC = () => {
  const [places, setPlaces] = useState<DiscoverPlaceItem[]>(MOCK_DISCOVER_PLACES);
  const [selectedId, setSelectedId] = useState<string>("my-khe-beach");
  const [mobileView, setMobileView] = useState<"list" | "map">("list");

  const handleSelectPlace = (id: string) => {
    setSelectedId(id);
  };

  const handleFavoriteToggle = (id: string, isFav: boolean) => {
    setPlaces((prev) =>
      prev.map((p) => (p.id === id ? { ...p, isFavorite: isFav } : p))
    );
  };

  const handleFilterChange = (filters: FilterState) => {
    let result = [...MOCK_DISCOVER_PLACES];

    if (filters.district !== "all") {
      if (filters.district === "son-tra") {
        result = result.filter((p) => p.district.includes("Sơn Trà"));
      } else if (filters.district === "hai-chau") {
        result = result.filter((p) => p.district.includes("Hải Châu"));
      } else if (filters.district === "hoa-vang") {
        result = result.filter((p) => p.district.includes("Hòa Vang"));
      }
    }

    if (filters.price === "free") {
      result = result.filter((p) => p.price.toLowerCase().includes("miễn phí"));
    } else if (filters.price === "cheap") {
      result = result.filter((p) => p.id === "cho-han");
    } else if (filters.price === "high") {
      result = result.filter((p) => p.id === "ba-na-hills");
    }

    setPlaces(result);

    // If selected place is no longer in filtered result, select first available
    if (result.length > 0 && !result.some((p) => p.id === selectedId)) {
      setSelectedId(result[0].id);
    }
  };

  const handleToggleMobileView = () => {
    setMobileView((prev) => (prev === "list" ? "map" : "list"));
  };

  return (
    <div className="vista-discover-page">
      {/* 1. PAGE HEADER */}
      <DiscoverHeader />

      {/* 2. HORIZONTAL FILTER BAR */}
      <DiscoverFilterBar
        onFilterChange={handleFilterChange}
        onViewMapClick={() => {
          setMobileView("map");
          // Smooth scroll to map on desktop if needed
          window.scrollTo({ top: 180, behavior: "smooth" });
        }}
      />

      {/* 3. MAIN SPLIT CONTENT AREA */}
      <main className="dnvibe-container vista-discover-main">
        <div className="vista-discover-split">
          {/* LEFT: PLACE LIST (46%) */}
          <div
            className={`vista-discover-split__left ${
              mobileView === "map" ? "vista-discover-split__left--hidden-mobile" : ""
            }`}
          >
            <DiscoverPlaceList
              places={places}
              selectedId={selectedId}
              onSelectPlace={handleSelectPlace}
              onFavoriteToggle={handleFavoriteToggle}
            />
          </div>

          {/* RIGHT: MAP PANEL (54%) */}
          <div
            className={`vista-discover-split__right ${
              mobileView === "list" ? "vista-discover-split__right--hidden-mobile" : ""
            }`}
          >
            <MapPanel
              places={places}
              selectedId={selectedId}
              onSelectPlace={handleSelectPlace}
            />
          </div>
        </div>
      </main>

      {/* MOBILE FLOATING VIEW TOGGLE BUTTON */}
      <div className="vista-discover-mobile-toggle">
        <button
          type="button"
          className="vista-discover-mobile-toggle__btn"
          onClick={handleToggleMobileView}
          aria-label={mobileView === "list" ? "Chuyển sang xem bản đồ" : "Chuyển sang xem danh sách"}
        >
          <span className="material-symbols-outlined text-base">
            {mobileView === "list" ? "map" : "format_list_bulleted"}
          </span>
          <span>{mobileView === "list" ? "Xem bản đồ" : "Xem danh sách"}</span>
        </button>
      </div>
    </div>
  );
};

export default DiscoverPage;
