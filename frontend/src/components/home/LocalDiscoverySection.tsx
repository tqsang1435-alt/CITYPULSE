import React, { useState, useEffect, useCallback } from "react";
import { Link } from "react-router-dom";
import PlaceCard from "../place/PlaceCard";
import { PlaceItem } from "../../data/mockPlaces";
import { placeService, mapSummaryToPlaceItem } from "../../services/placeService";
import "./LocalDiscoverySection.css";

export const LocalDiscoverySection: React.FC = () => {
  const [places, setPlaces] = useState<PlaceItem[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState("all");

  const loadPlaces = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      // Fetch 6 top-rated places from backend API
      const res = await placeService.listPlaces({
        page: 0,
        size: 6,
        sort: "rating",
      });
      const items = res.content.map((summary) => mapSummaryToPlaceItem(summary));
      setPlaces(items);
    } catch (err: unknown) {
      setError("Không thể tải danh sách địa điểm từ máy chủ.");
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    loadPlaces();
  }, [loadPlaces]);

  const filteredPlaces = places.filter((p) => {
    const cat = (p.categoryTag || "").toLowerCase();
    const name = (p.name || "").toLowerCase();
    const id = (p.id || "").toLowerCase();

    if (activeTab === "beach") {
      return cat.includes("biển") || name.includes("mỹ khê") || name.includes("non nước") || name.includes("rạng");
    }
    if (activeTab === "mountain") {
      return cat.includes("núi") || id.includes("ba-na") || id.includes("son-tra") || name.includes("linh ứng") || name.includes("ngũ hành sơn");
    }
    if (activeTab === "heritage") {
      return cat.includes("ẩm thực") || cat.includes("chợ") || cat.includes("bảo tàng") || cat.includes("văn hóa") || name.includes("cầu");
    }
    return true;
  });

  return (
    <section className="vista-discovery-section dnvibe-container">
      {/* HEADER */}
      <div className="vista-discovery-section__header">
        <div className="vista-discovery-section__title-col">
          <div className="vista-discovery-section__badge">
            <span className="material-symbols-outlined text-sm">verified</span>
            <span>Dữ liệu thực tế từ cộng đồng du lịch</span>
          </div>
          <h2 className="vista-discovery-section__title">Khám phá như người địa phương</h2>
          <p className="vista-discovery-section__desc">
            Những địa điểm được yêu thích bởi người Đà Nẵng, không chỉ dành cho du khách.
          </p>
        </div>

        {/* RIGHT CONTROLS: FILTER PILLS + SEE ALL LINK */}
        <div className="vista-discovery-section__actions">
          <div className="vista-discovery-section__tabs" role="tablist">
            <button
              type="button"
              className={`vista-discovery-section__tab ${activeTab === "all" ? "vista-discovery-section__tab--active" : ""}`}
              onClick={() => setActiveTab("all")}
            >
              Tất cả
            </button>
            <button
              type="button"
              className={`vista-discovery-section__tab ${activeTab === "beach" ? "vista-discovery-section__tab--active" : ""}`}
              onClick={() => setActiveTab("beach")}
            >
              Bãi biển
            </button>
            <button
              type="button"
              className={`vista-discovery-section__tab ${activeTab === "mountain" ? "vista-discovery-section__tab--active" : ""}`}
              onClick={() => setActiveTab("mountain")}
            >
              Lên núi cao
            </button>
            <button
              type="button"
              className={`vista-discovery-section__tab ${activeTab === "heritage" ? "vista-discovery-section__tab--active" : ""}`}
              onClick={() => setActiveTab("heritage")}
            >
              Chợ & Ẩm thực
            </button>
          </div>

          <Link to="/places" className="vista-discovery-section__see-all">
            <span>Xem tất cả</span>
            <span className="material-symbols-outlined text-base">arrow_forward</span>
          </Link>
        </div>
      </div>

      {/* SKELETON LOADING STATE */}
      {isLoading && (
        <div className="vista-discovery-section__grid">
          {[1, 2, 3].map((n) => (
            <div key={n} className="vista-discovery-skeleton">
              <div className="vista-discovery-skeleton__media" />
              <div className="vista-discovery-skeleton__body">
                <div className="vista-discovery-skeleton__line" style={{ width: "40%" }} />
                <div className="vista-discovery-skeleton__line" style={{ width: "75%", height: "20px" }} />
                <div className="vista-discovery-skeleton__line" style={{ width: "90%" }} />
                <div className="vista-discovery-skeleton__line" style={{ width: "60%", marginTop: "auto" }} />
              </div>
            </div>
          ))}
        </div>
      )}

      {/* ERROR STATE */}
      {!isLoading && error && (
        <div className="vista-discovery-section__status-msg">
          <span className="material-symbols-outlined text-3xl" style={{ color: "#ef4444" }}>
            cloud_off
          </span>
          <p>{error}</p>
          <button
            type="button"
            onClick={loadPlaces}
            className="vista-auth-card__submit-btn"
            style={{ width: "auto", padding: "8px 20px" }}
          >
            Thử lại
          </button>
        </div>
      )}

      {/* EMPTY STATE */}
      {!isLoading && !error && filteredPlaces.length === 0 && (
        <div className="vista-discovery-section__status-msg">
          <span className="material-symbols-outlined text-3xl text-primary">explore</span>
          <p>Không có địa điểm nào trong danh mục này.</p>
        </div>
      )}

      {/* REAL PLACES GRID */}
      {!isLoading && !error && filteredPlaces.length > 0 && (
        <div className="vista-discovery-section__grid">
          {filteredPlaces.slice(0, 3).map((place) => (
            <PlaceCard key={place.id} place={place} />
          ))}
        </div>
      )}
    </section>
  );
};

export default LocalDiscoverySection;
