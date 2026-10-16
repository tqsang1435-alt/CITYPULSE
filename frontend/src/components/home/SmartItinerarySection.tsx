import React from "react";
import { Link } from "react-router-dom";
import { MOCK_ITINERARY_STOPS } from "../../data/mockPlaces";
import "./SmartItinerarySection.css";

export const SmartItinerarySection: React.FC = () => {
  return (
    <section className="vista-itinerary-section">
      {/* AMBIENT BACKGROUND GLOWS */}
      <div className="vista-itinerary-section__glow-1" aria-hidden="true" />
      <div className="vista-itinerary-section__glow-2" aria-hidden="true" />

      <div className="dnvibe-container vista-itinerary-section__container">
        {/* SECTION HEADER */}
        <div className="vista-itinerary-section__header text-center">
          <div className="vista-itinerary-section__badge">
            <span className="material-symbols-outlined text-sm">route</span>
            <span>VISTA Smart Route Engine 2.0</span>
          </div>
          <h2 className="vista-itinerary-section__title">
            Lịch trình mẫu 1 Ngày: “Biển Xanh &amp; Phố Đêm”
          </h2>
          <p className="vista-itinerary-section__desc">
            Được cá nhân hóa tự động theo thuật toán tính toán mật độ khách và khoảng cách di chuyển ngắn nhất.
          </p>
        </div>

        {/* 2-COLUMN SPLIT: LEFT TIMELINE, RIGHT CREATION LAUNCHPAD */}
        <div className="vista-itinerary-section__layout">
          {/* TIMELINE COLUMN */}
          <div className="vista-itinerary-section__timeline-col">
            <div className="vista-itinerary-timeline">
              {MOCK_ITINERARY_STOPS.map((stop, index) => {
                const isLast = index === MOCK_ITINERARY_STOPS.length - 1;
                return (
                  <div key={stop.step} className="vista-itinerary-step">
                    {!isLast && <div className="vista-itinerary-step__line" aria-hidden="true" />}
                    <div
                      className={`vista-itinerary-step__marker ${
                        isLast ? "vista-itinerary-step__marker--highlight" : ""
                      }`}
                    >
                      {stop.step}
                    </div>

                    <div className="vista-itinerary-step__content">
                      <div className="vista-itinerary-step__meta">
                        <span className="vista-itinerary-step__time">{stop.timeSlot}</span>
                        <span className="vista-itinerary-step__dur">Thời lượng: {stop.duration}</span>
                      </div>
                      <h4 className="vista-itinerary-step__title">{stop.title}</h4>
                      <p className="vista-itinerary-step__desc">{stop.description}</p>
                      <div className="vista-itinerary-step__transit">
                        <span className="material-symbols-outlined text-xs">two_wheeler</span>
                        <span>{stop.transitInfo}</span>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* LAUNCHPAD & STATS COLUMN */}
          <div className="vista-itinerary-section__sidebar-col">
            {/* STATS OVERVIEW CARD */}
            <div className="vista-itinerary-stats-card">
              <h4 className="vista-itinerary-stats-card__title">
                <span className="material-symbols-outlined text-primary text-lg">insights</span>
                <span>Tổng quan hành trình</span>
              </h4>

              <div className="vista-itinerary-stats-grid">
                <div className="vista-itinerary-stat-box">
                  <span className="vista-itinerary-stat-label">Tổng chi phí ước tính</span>
                  <span className="vista-itinerary-stat-value text-primary">~350.000đ</span>
                  <span className="vista-itinerary-stat-sub">Ăn uống & xăng xe</span>
                </div>
                <div className="vista-itinerary-stat-box vista-itinerary-stat-box--highlight">
                  <span className="vista-itinerary-stat-label">Chỉ số thoải mái</span>
                  <span className="vista-itinerary-stat-value text-secondary">96 / 100</span>
                  <span className="vista-itinerary-stat-sub text-secondary">Tránh nắng & vắng xe</span>
                </div>
                <div className="vista-itinerary-stat-box">
                  <span className="vista-itinerary-stat-label">Tổng quãng đường</span>
                  <span className="vista-itinerary-stat-value">18.4 km</span>
                  <span className="vista-itinerary-stat-sub">Vòng cung ven biển</span>
                </div>
                <div className="vista-itinerary-stat-box">
                  <span className="vista-itinerary-stat-label">Thời gian trải nghiệm</span>
                  <span className="vista-itinerary-stat-value">8.5 tiếng</span>
                  <span className="vista-itinerary-stat-sub">Nghỉ trưa 2 tiếng</span>
                </div>
              </div>

              {/* ROUTE DISTRIBUTION PROGRESS BAR */}
              <div className="vista-itinerary-distribution">
                <div className="vista-itinerary-distribution__label">
                  <span>Tỷ lệ trải nghiệm</span>
                  <strong>50% Biển • 30% Ẩm thực • 20% Phố đêm</strong>
                </div>
                <div className="vista-itinerary-distribution__bar">
                  <div className="vista-itinerary-distribution__seg vista-itinerary-distribution__seg--1" />
                  <div className="vista-itinerary-distribution__seg vista-itinerary-distribution__seg--2" />
                  <div className="vista-itinerary-distribution__seg vista-itinerary-distribution__seg--3" />
                </div>
              </div>
            </div>

            {/* CTA LAUNCHPAD CARD */}
            <div className="vista-itinerary-cta-card">
              <span className="vista-itinerary-cta-card__badge">LÊN KẾ HOẠCH DỄ DÀNG</span>
              <h3 className="vista-itinerary-cta-card__title">Tạo lịch trình thông minh</h3>
              <p className="vista-itinerary-cta-card__desc">
                Chỉ cần chọn thời gian, ngân sách và sở thích, VISTA sẽ gợi ý lịch trình tối ưu, giúp bạn khám phá Đà Nẵng theo cách riêng.
              </p>

              <Link to="/trips" className="vista-itinerary-cta-card__btn">
                <span>Bắt đầu tạo lịch trình</span>
                <span className="material-symbols-outlined text-base">bolt</span>
              </Link>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default SmartItinerarySection;
