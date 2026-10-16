import React from "react";
import DiscoverPlaceCard from "./DiscoverPlaceCard";
import { DiscoverPlaceItem } from "../../data/mockPlaces";
import "./DiscoverPlaceList.css";

export interface DiscoverPlaceListProps {
  places: DiscoverPlaceItem[];
  selectedId: string;
  onSelectPlace: (id: string) => void;
  onFavoriteToggle?: (id: string, isFav: boolean) => void;
  totalElements: number;
  page: number;
  totalPages: number;
  sortBy: string;
  onSortChange: (sort: string) => void;
  onPageChange: (newPage: number) => void;
  isLoading?: boolean;
  first?: boolean;
  last?: boolean;
}

export const DiscoverPlaceList: React.FC<DiscoverPlaceListProps> = ({
  places,
  selectedId,
  onSelectPlace,
  onFavoriteToggle,
  totalElements,
  page,
  totalPages,
  sortBy,
  onSortChange,
  onPageChange,
  isLoading = false,
  first = true,
  last = true,
}) => {
  return (
    <section className="vista-discover-list">
      {/* LIST HEADER: TOTAL COUNT & SORT DROPDOWN */}
      <div className="vista-discover-list__header">
        <h2 className="vista-discover-list__count">
          Có {totalElements} địa điểm tại Đà Nẵng
        </h2>

        <div className="vista-discover-list__sort">
          <label htmlFor="sort-places" className="vista-discover-list__sort-label">
            Sắp xếp:
          </label>
          <div className="vista-discover-list__sort-wrapper">
            <select
              id="sort-places"
              value={sortBy}
              onChange={(e) => onSortChange(e.target.value)}
              className="vista-discover-list__sort-select"
            >
              <option value="rating">Đánh giá cao nhất</option>
              <option value="rating,asc">Đánh giá thấp nhất</option>
              <option value="price">Giá rẻ nhất</option>
              <option value="price,desc">Giá cao nhất</option>
              <option value="name">Tên (A-Z)</option>
              <option value="newest">Mới nhất</option>
            </select>
            <span className="material-symbols-outlined vista-discover-list__sort-arrow">
              expand_more
            </span>
          </div>
        </div>
      </div>

      {/* SKELETON LOADING STATE */}
      {isLoading && (
        <div className="vista-discover-list__cards">
          {[1, 2, 3, 4].map((n) => (
            <div key={n} className="vista-discover-skeleton-card">
              <div className="vista-discover-skeleton-card__thumb" />
              <div className="vista-discover-skeleton-card__body">
                <div className="vista-discover-skeleton-card__line" style={{ width: "50%" }} />
                <div className="vista-discover-skeleton-card__line" style={{ width: "80%" }} />
                <div className="vista-discover-skeleton-card__line" style={{ width: "40%" }} />
                <div className="vista-discover-skeleton-card__line" style={{ width: "60%", marginTop: "auto" }} />
              </div>
            </div>
          ))}
        </div>
      )}

      {/* EMPTY RESULTS STATE */}
      {!isLoading && places.length === 0 && (
        <div className="vista-discover-empty">
          <span className="material-symbols-outlined text-4xl text-primary">search_off</span>
          <p style={{ fontWeight: 600, fontSize: "16px", margin: 0 }}>
            Không tìm thấy địa điểm nào phù hợp
          </p>
          <p style={{ fontSize: "13.5px", margin: 0, color: "var(--dnvibe-neutral-500)" }}>
            Hãy thử thay đổi từ khóa tìm kiếm hoặc đặt lại các tiêu chí bộ lọc.
          </p>
        </div>
      )}

      {/* REAL PLACES STACK */}
      {!isLoading && places.length > 0 && (
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
      )}

      {/* PAGINATION CONTROLS */}
      {totalPages > 1 && (
        <div className="vista-discover-list__pagination" aria-label="Điều hướng phân trang">
          <button
            type="button"
            className="vista-discover-list__page-btn"
            disabled={first || page <= 0 || isLoading}
            onClick={() => onPageChange(page - 1)}
            aria-label="Trang trước"
          >
            <span className="material-symbols-outlined text-sm">chevron_left</span>
            <span>Trang trước</span>
          </button>

          <span className="vista-discover-list__page-info">
            Trang {page + 1} / {totalPages}
          </span>

          <button
            type="button"
            className="vista-discover-list__page-btn"
            disabled={last || page >= totalPages - 1 || isLoading}
            onClick={() => onPageChange(page + 1)}
            aria-label="Trang sau"
          >
            <span>Trang sau</span>
            <span className="material-symbols-outlined text-sm">chevron_right</span>
          </button>
        </div>
      )}
    </section>
  );
};

export default DiscoverPlaceList;
