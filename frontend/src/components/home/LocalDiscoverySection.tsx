import React, { useState } from "react";
import { Link } from "react-router-dom";
import PlaceCard from "../place/PlaceCard";
import { MOCK_LOCAL_PLACES, PlaceItem } from "../../data/mockPlaces";
import "./LocalDiscoverySection.css";

export const LocalDiscoverySection: React.FC = () => {
  const [places, setPlaces] = useState<PlaceItem[]>(MOCK_LOCAL_PLACES);
  const [activeTab, setActiveTab] = useState("all");

  const handleFavoriteToggle = (id: string, isFav: boolean) => {
    setPlaces((prev) =>
      prev.map((p) => (p.id === id ? { ...p, isFavorite: isFav } : p))
    );
  };

  const filteredPlaces = places.filter((p) => {
    if (activeTab === "beach") return p.categoryTag.includes("Biển");
    if (activeTab === "mountain") return p.categoryTag.includes("Núi") || p.id === "ba-na-hills";
    if (activeTab === "heritage") return p.categoryTag.includes("Ẩm thực") || p.id === "cho-han";
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

      {/* 3 PLACE CARDS GRID */}
      <div className="vista-discovery-section__grid">
        {filteredPlaces.map((place) => (
          <PlaceCard
            key={place.id}
            place={place}
            onFavoriteToggle={handleFavoriteToggle}
          />
        ))}
      </div>
    </section>
  );
};

export default LocalDiscoverySection;
