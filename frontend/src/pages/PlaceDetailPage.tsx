import React, { useEffect, useState, useCallback } from "react";
import { Link, useParams, useNavigate, useLocation } from "react-router-dom";
import { PlaceDetailResponse } from "../types/api";
import { placeService, formatPrice, parseTags } from "../services/placeService";
import { useFavorite } from "../context/FavoriteContext";
import { useAuth } from "../context/AuthContext";
import "./PlaceDetailPage.css";

export const PlaceDetailPage: React.FC = () => {
  const { idOrSlug } = useParams<{ idOrSlug: string }>();
  const navigate = useNavigate();
  const location = useLocation();

  const { isAuthenticated } = useAuth();
  const { isFavorited, toggleFavorite } = useFavorite();

  const [place, setPlace] = useState<PlaceDetailResponse | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const fetchDetail = useCallback(async () => {
    if (!idOrSlug) return;
    setIsLoading(true);
    setError(null);
    try {
      const data = await placeService.getPlaceByIdOrSlug(idOrSlug);
      setPlace(data);
    } catch {
      setError("Không tìm thấy địa điểm hoặc địa điểm chưa được công khai.");
    } finally {
      setIsLoading(false);
    }
  }, [idOrSlug]);

  useEffect(() => {
    fetchDetail();
  }, [fetchDetail]);

  const isFav = place
    ? isFavorited(place.id) || (place.slug ? isFavorited(place.slug) : false)
    : false;

  const handleFavoriteToggle = async () => {
    if (!place) return;
    if (!isAuthenticated) {
      navigate("/login", { state: { from: location } });
      return;
    }
    try {
      await toggleFavorite(place.id, place.slug);
    } catch {
      // Handled by context rollback
    }
  };

  if (isLoading) {
    return (
      <div className="vista-detail-page dnvibe-container" style={{ paddingTop: "40px" }}>
        <div
          className="vista-discovery-skeleton"
          style={{ height: "400px", borderRadius: "24px", marginBottom: "32px" }}
        />
        <div style={{ display: "grid", gridTemplateColumns: "repeat(4, 1fr)", gap: "16px", marginBottom: "32px" }}>
          {[1, 2, 3, 4].map((n) => (
            <div key={n} className="vista-discovery-skeleton" style={{ height: "80px", borderRadius: "16px" }} />
          ))}
        </div>
      </div>
    );
  }

  if (error || !place) {
    return (
      <div className="vista-detail-page dnvibe-container" style={{ paddingTop: "80px", textAlign: "center" }}>
        <div className="vista-discover-empty" style={{ maxWidth: "500px", margin: "0 auto", padding: "48px 24px" }}>
          <span className="material-symbols-outlined text-5xl" style={{ color: "#ef4444" }}>
            location_off
          </span>
          <h2 style={{ fontSize: "22px", fontWeight: 700, margin: "8px 0" }}>
            Địa điểm không tồn tại
          </h2>
          <p style={{ color: "var(--dnvibe-neutral-600)", marginBottom: "20px" }}>
            {error || "Địa điểm bạn tìm kiếm không có trong hệ thống hoặc đã tạm ngừng phục vụ."}
          </p>
          <Link
            to="/places"
            className="vista-auth-card__submit-btn"
            style={{ textDecoration: "none", width: "auto", padding: "10px 24px" }}
          >
            ← Quay lại Khám phá
          </Link>
        </div>
      </div>
    );
  }

  const tags = parseTags(place.tags);
  const formattedPrice = formatPrice(Number(place.priceMin) || 0, Number(place.priceMax) || 0);
  const coverUrl = place.coverImageUrl || place.thumbnailUrl || "/images/my-khe.png";

  return (
    <div className="vista-detail-page">
      <div className="dnvibe-container">
        {/* BREADCRUMB */}
        <nav className="vista-detail-breadcrumb" aria-label="Đường dẫn trang">
          <Link to="/" className="vista-detail-breadcrumb__link">
            Trang chủ
          </Link>
          <span>/</span>
          <Link to="/places" className="vista-detail-breadcrumb__link">
            Khám phá
          </Link>
          <span>/</span>
          <span className="vista-detail-breadcrumb__current">{place.name}</span>
        </nav>

        {/* HERO COVER BANNER */}
        <div className="vista-detail-hero">
          <img src={coverUrl} alt={place.name} className="vista-detail-hero__img" />
          <div className="vista-detail-hero__gradient">
            <div className="vista-detail-hero__top">
              <Link to="/places" className="vista-detail-hero__back-btn">
                <span className="material-symbols-outlined text-sm">arrow_back</span>
                <span>Khám phá Đà Nẵng</span>
              </Link>

              <button
                type="button"
                className={`vista-detail-hero__fav-btn ${isFav ? "vista-detail-hero__fav-btn--active" : ""}`}
                onClick={handleFavoriteToggle}
                aria-label={isFav ? "Bỏ lưu địa điểm" : "Lưu vào danh sách yêu thích"}
                title={isFav ? "Đã lưu địa điểm" : "Lưu địa điểm này"}
              >
                <span
                  className="material-symbols-outlined text-xl"
                  style={isFav ? { fontVariationSettings: "'FILL' 1" } : undefined}
                >
                  favorite
                </span>
              </button>
            </div>

            <div className="vista-detail-hero__bottom">
              <div className="vista-detail-hero__badges">
                <span className="vista-detail-hero__badge">
                  <span className="material-symbols-outlined text-sm text-primary">place</span>
                  <span>{place.district}</span>
                </span>
                <span className="vista-detail-hero__badge">
                  <span className="material-symbols-outlined text-sm text-secondary">category</span>
                  <span>{place.category}</span>
                </span>
                <span className="vista-detail-hero__badge">
                  <span
                    className="material-symbols-outlined text-sm text-secondary"
                    style={{ fontVariationSettings: "'FILL' 1" }}
                  >
                    star
                  </span>
                  <span>{(Number(place.ratingAverage) || 4.5).toFixed(1)} ({place.reviewCount || 0} đánh giá)</span>
                </span>
              </div>

              <h1 className="vista-detail-hero__title">{place.name}</h1>

              <div className="vista-detail-hero__address">
                <span className="material-symbols-outlined text-base">location_on</span>
                <span>{place.address}</span>
              </div>
            </div>
          </div>
        </div>

        {/* QUICK SPECS GRID */}
        <div className="vista-detail-specs">
          {/* 1. THỜI ĐIỂM ĐẸP NHẤT */}
          <div className="vista-detail-spec-card">
            <div className="vista-detail-spec-card__icon-wrap">
              <span className="material-symbols-outlined">wb_sunny</span>
            </div>
            <div>
              <div className="vista-detail-spec-card__label">Thời điểm đẹp nhất</div>
              <div className="vista-detail-spec-card__val">{place.bestTime || "Quanh năm"}</div>
            </div>
          </div>

          {/* 2. CHI PHÍ DỰ KIẾN */}
          <div className="vista-detail-spec-card">
            <div className="vista-detail-spec-card__icon-wrap">
              <span className="material-symbols-outlined">payments</span>
            </div>
            <div>
              <div className="vista-detail-spec-card__label">Chi phí dự kiến</div>
              <div className="vista-detail-spec-card__val">{formattedPrice}</div>
            </div>
          </div>

          {/* 3. THỜI LƯỢNG */}
          <div className="vista-detail-spec-card">
            <div className="vista-detail-spec-card__icon-wrap">
              <span className="material-symbols-outlined">timer</span>
            </div>
            <div>
              <div className="vista-detail-spec-card__label">Thời lượng tham quan</div>
              <div className="vista-detail-spec-card__val">{place.visitDurationMin || 90} phút</div>
            </div>
          </div>

          {/* 4. TỌA ĐỘ GPS */}
          <div className="vista-detail-spec-card">
            <div className="vista-detail-spec-card__icon-wrap">
              <span className="material-symbols-outlined">my_location</span>
            </div>
            <div>
              <div className="vista-detail-spec-card__label">Tọa độ GPS</div>
              <div className="vista-detail-spec-card__val">
                {place.latitude != null && place.longitude != null
                  ? `${place.latitude.toFixed(4)}, ${place.longitude.toFixed(4)}`
                  : "Đà Nẵng"}
              </div>
            </div>
          </div>
        </div>

        {/* MAIN BODY: 2 COLUMNS */}
        <div className="vista-detail-body">
          {/* LEFT: DETAILED INFO */}
          <div>
            {/* SHORT INTRO */}
            {place.shortDescription && (
              <section className="vista-detail-section">
                <h2 className="vista-detail-section__title">
                  <span className="material-symbols-outlined text-primary">info</span>
                  <span>Tổng quan</span>
                </h2>
                <p className="vista-detail-section__desc">{place.shortDescription}</p>
              </section>
            )}

            {/* LONG DESCRIPTION */}
            {place.longDescription && (
              <section className="vista-detail-section">
                <h2 className="vista-detail-section__title">
                  <span className="material-symbols-outlined text-primary">article</span>
                  <span>Chi tiết trải nghiệm</span>
                </h2>
                <p className="vista-detail-section__desc">{place.longDescription}</p>

                {tags.length > 0 && (
                  <div className="vista-detail-tags">
                    {tags.map((tag) => (
                      <span key={tag} className="vista-detail-tag">
                        #{tag}
                      </span>
                    ))}
                  </div>
                )}
              </section>
            )}
          </div>

          {/* RIGHT SIDEBAR ACTIONS */}
          <aside className="vista-detail-sidebar">
            <div className="vista-detail-sidebar-card">
              <h3 className="vista-detail-sidebar-card__title">Định vị điểm đến</h3>
              <p style={{ fontSize: "13.5px", color: "var(--dnvibe-neutral-600)", marginBottom: "18px" }}>
                Xem vị trí {place.name} trực quan trên bản đồ bờ vịnh Đà Nẵng cùng các danh thắng lân cận.
              </p>
              <Link
                to={`/places?id=${place.slug || place.id}&view=map`}
                className="vista-detail-map-preview-btn"
              >
                <span className="material-symbols-outlined text-base">map</span>
                <span>Xem trên bản đồ</span>
              </Link>
            </div>

            <div className="vista-detail-sidebar-card">
              <h3 className="vista-detail-sidebar-card__title">Lưu vào bộ sưu tập</h3>
              <p style={{ fontSize: "13.5px", color: "var(--dnvibe-neutral-600)", marginBottom: "18px" }}>
                {isFav
                  ? "Địa điểm này đã nằm trong danh sách yêu thích của bạn."
                  : "Lưu lại để dễ dàng đưa vào lịch trình du lịch Đà Nẵng của bạn."}
              </p>
              <button
                type="button"
                onClick={handleFavoriteToggle}
                className="vista-auth-card__submit-btn"
                style={{
                  background: isFav
                    ? "linear-gradient(135deg, #ef4444 0%, #dc2626 100%)"
                    : "linear-gradient(135deg, var(--dnvibe-primary) 0%, #158b88 100%)",
                }}
              >
                <span
                  className="material-symbols-outlined text-base"
                  style={isFav ? { fontVariationSettings: "'FILL' 1" } : undefined}
                >
                  favorite
                </span>
                <span>{isFav ? "Đã lưu trong yêu thích" : "Lưu vào yêu thích"}</span>
              </button>
            </div>
          </aside>
        </div>
      </div>
    </div>
  );
};

export default PlaceDetailPage;
