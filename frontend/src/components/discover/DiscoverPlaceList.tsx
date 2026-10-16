import React, { useState } from "react";
import DiscoverPlaceCard from "./DiscoverPlaceCard";
import { DiscoverPlaceItem } from "../../data/mockPlaces";
import "./DiscoverPlaceList.css";

export interface DiscoverPlaceListProps {
  places: DiscoverPlaceItem[];
  selectedId: string;
  onSelectPlace: (id: string) => void;
  onFavoriteToggle: (id: string, isFav: boolean) => void;
}

export const DiscoverPlaceList: React.FC<DiscoverPlaceListProps> = ({
  places,
  selectedId,
  onSelectPlace,
  onFavoriteToggle,
}) => {
  const [sortBy, setSortBy] = useState("relevant");

  return (
    <section className="vista-discover-list">
      {/* LIST HEADER: TOTAL COUNT & SORT DROPDOWN */}
      <div className="vista-discover-list__header">
        <h2 className="vista-discover-list__count">Có 120+ địa điểm tại Đà Nẵng</h2>

        <div className="vista-discover-list__sort">
          <label htmlFor="sort-places" className="vista-discover-list__sort-label">
            Sắp xếp:
          </label>
          <div className="vista-discover-list__sort-wrapper">
            <select
              id="sort-places"
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
              className="vista-discover-list__sort-select"
            >
              <option value="relevant">Phù hợp nhất</option>
              <option value="rating">Đánh giá cao nhất</option>
              <option value="price_asc">Giá rẻ nhất</option>
              <option value="distance">Gần tôi nhất</option>
            </select>
            <span className="material-symbols-outlined vista-discover-list__sort-arrow">
              expand_more
            </span>
          </div>
        </div>
      </div>

      {/* PLACES STACK */}
      <div className="vista-discover-list__cards">
        {places.map((place) => (
          <DiscoverPlaceCard
            key={place.id}
            place={place}
            isSelected={place.id === selectedId}
            onSelect={onSelectPlace}
            onFavoriteToggle={onFavoriteToggle}
          />
        ))}
      </div>
    </section>
  );
};

export default DiscoverPlaceList;
