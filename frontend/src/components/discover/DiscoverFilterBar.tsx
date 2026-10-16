import React, { useState } from "react";
import "./DiscoverFilterBar.css";

export interface FilterState {
  district: string;
  price: string;
  status: string;
  time: string;
  distance: string;
}

export interface DiscoverFilterBarProps {
  onFilterChange?: (filters: FilterState) => void;
  onViewMapClick?: () => void;
}

export const DiscoverFilterBar: React.FC<DiscoverFilterBarProps> = ({
  onFilterChange,
  onViewMapClick,
}) => {
  const defaultFilters: FilterState = {
    district: "all",
    price: "all",
    status: "all",
    time: "all",
    distance: "all",
  };

  const [filters, setFilters] = useState<FilterState>(defaultFilters);

  const handleSelectChange = (key: keyof FilterState, value: string) => {
    const updated = { ...filters, [key]: value };
    setFilters(updated);
    if (onFilterChange) {
      onFilterChange(updated);
    }
  };

  const handleReset = () => {
    setFilters(defaultFilters);
    if (onFilterChange) {
      onFilterChange(defaultFilters);
    }
  };

  return (
    <div className="vista-filter-bar-wrapper dnvibe-container">
      <div className="vista-filter-bar">
        {/* 5 HORIZONTAL FILTER UNITS */}
        <div className="vista-filter-bar__items">
          {/* 1. KHU VỰC */}
          <div className="vista-filter-item">
            <div className="vista-filter-item__icon-wrap">
              <span className="material-symbols-outlined text-primary">location_on</span>
            </div>
            <div className="vista-filter-item__content">
              <label htmlFor="filter-district" className="vista-filter-item__label">
                Khu vực
              </label>
              <div className="vista-filter-item__select-wrap">
                <select
                  id="filter-district"
                  value={filters.district}
                  onChange={(e) => handleSelectChange("district", e.target.value)}
                  className="vista-filter-item__select"
                >
                  <option value="all">Tất cả khu vực</option>
                  <option value="son-tra">Quận Sơn Trà</option>
                  <option value="hai-chau">Quận Hải Châu</option>
                  <option value="hoa-vang">Huyện Hòa Vang</option>
                  <option value="ngu-hanh-son">Quận Ngũ Hành Sơn</option>
                </select>
                <span className="material-symbols-outlined vista-filter-item__arrow">expand_more</span>
              </div>
            </div>
          </div>

          <div className="vista-filter-bar__divider" aria-hidden="true" />

          {/* 2. GIÁ */}
          <div className="vista-filter-item">
            <div className="vista-filter-item__icon-wrap">
              <span className="material-symbols-outlined text-primary">sell</span>
            </div>
            <div className="vista-filter-item__content">
              <label htmlFor="filter-price" className="vista-filter-item__label">
                Giá
              </label>
              <div className="vista-filter-item__select-wrap">
                <select
                  id="filter-price"
                  value={filters.price}
                  onChange={(e) => handleSelectChange("price", e.target.value)}
                  className="vista-filter-item__select"
                >
                  <option value="all">Tất cả mức giá</option>
                  <option value="free">Miễn phí</option>
                  <option value="cheap">Dưới 100.000đ</option>
                  <option value="medium">100k – 500k</option>
                  <option value="high">Trên 500.000đ</option>
                </select>
                <span className="material-symbols-outlined vista-filter-item__arrow">expand_more</span>
              </div>
            </div>
          </div>

          <div className="vista-filter-bar__divider" aria-hidden="true" />

          {/* 3. ĐANG MỞ */}
          <div className="vista-filter-item">
            <div className="vista-filter-item__icon-wrap">
              <span className="material-symbols-outlined text-primary">schedule</span>
            </div>
            <div className="vista-filter-item__content">
              <label htmlFor="filter-status" className="vista-filter-item__label">
                Đang mở
              </label>
              <div className="vista-filter-item__select-wrap">
                <select
                  id="filter-status"
                  value={filters.status}
                  onChange={(e) => handleSelectChange("status", e.target.value)}
                  className="vista-filter-item__select"
                >
                  <option value="all">Tất cả</option>
                  <option value="open_now">Đang mở cửa</option>
                  <option value="247">Mở 24/7</option>
                </select>
                <span className="material-symbols-outlined vista-filter-item__arrow">expand_more</span>
              </div>
            </div>
          </div>

          <div className="vista-filter-bar__divider" aria-hidden="true" />

          {/* 4. BEST TIME */}
          <div className="vista-filter-item">
            <div className="vista-filter-item__icon-wrap">
              <span className="material-symbols-outlined text-primary">calendar_month</span>
            </div>
            <div className="vista-filter-item__content">
              <label htmlFor="filter-time" className="vista-filter-item__label">
                Best time
              </label>
              <div className="vista-filter-item__select-wrap">
                <select
                  id="filter-time"
                  value={filters.time}
                  onChange={(e) => handleSelectChange("time", e.target.value)}
                  className="vista-filter-item__select"
                >
                  <option value="all">Tất cả thời điểm</option>
                  <option value="morning">Sáng – Hoàng hôn</option>
                  <option value="daytime">Ban ngày (Nắng đẹp)</option>
                  <option value="night">Về đêm (Cuối tuần)</option>
                </select>
                <span className="material-symbols-outlined vista-filter-item__arrow">expand_more</span>
              </div>
            </div>
          </div>

          <div className="vista-filter-bar__divider" aria-hidden="true" />

          {/* 5. KHOẢNG CÁCH */}
          <div className="vista-filter-item">
            <div className="vista-filter-item__icon-wrap">
              <span className="material-symbols-outlined text-primary">directions_walk</span>
            </div>
            <div className="vista-filter-item__content">
              <label htmlFor="filter-distance" className="vista-filter-item__label">
                Khoảng cách
              </label>
              <div className="vista-filter-item__select-wrap">
                <select
                  id="filter-distance"
                  value={filters.distance}
                  onChange={(e) => handleSelectChange("distance", e.target.value)}
                  className="vista-filter-item__select"
                >
                  <option value="all">Tất cả</option>
                  <option value="2km">&lt; 2 km</option>
                  <option value="5km">2 – 5 km</option>
                  <option value="10km">&gt; 5 km</option>
                </select>
                <span className="material-symbols-outlined vista-filter-item__arrow">expand_more</span>
              </div>
            </div>
          </div>
        </div>

        {/* RIGHT SIDE: RESET & CTA "XEM TRÊN BẢN ĐỒ" */}
        <div className="vista-filter-bar__actions">
          <button
            type="button"
            onClick={handleReset}
            className="vista-filter-bar__reset-btn"
            title="Đặt lại toàn bộ bộ lọc"
          >
            <span className="material-symbols-outlined text-sm">restart_alt</span>
            <span>Xóa bộ lọc</span>
          </button>

          <button
            type="button"
            onClick={onViewMapClick}
            className="vista-filter-bar__cta-btn"
            title="Xem hiển thị trên bản đồ"
          >
            <span className="material-symbols-outlined text-base">map</span>
            <span>Xem trên bản đồ</span>
          </button>
        </div>
      </div>
    </div>
  );
};

export default DiscoverFilterBar;
