import React from "react";
import { Link } from "react-router-dom";
import "./CommunityBanner.css";

export const CommunityBanner: React.FC = () => {
  return (
    <section className="vista-community-banner dnvibe-container">
      <div className="vista-community-banner__inner">
        <div className="vista-community-banner__content">
          {/* 5 STARS */}
          <div className="vista-community-banner__stars">
            {[1, 2, 3, 4, 5].map((i) => (
              <span
                key={i}
                className="material-symbols-outlined text-sm"
                style={{ fontVariationSettings: "'FILL' 1" }}
              >
                star
              </span>
            ))}
          </div>

          <h3 className="vista-community-banner__title">
            Được hơn 45.000+ du khách tin cậy mỗi tháng
          </h3>

          <p className="vista-community-banner__quote">
            “Nhờ VISTA mà mình chỉ có đúng 5 tiếng quá cảnh ở Đà Nẵng nhưng vừa kịp tắm biển Mỹ Khê, ăn tô Mì Quảng chuẩn vị và ngắm hoàng hôn Sơn Trà tuyệt đẹp mà không bị lố giờ ra sân bay!”
          </p>

          <div className="vista-community-banner__author">
            <div className="vista-community-banner__avatar">NL</div>
            <div>
              <span className="vista-community-banner__author-name">Ngọc Linh</span>
              <span className="vista-community-banner__author-loc">Du khách từ TP. Hồ Chí Minh</span>
            </div>
          </div>
        </div>

        <div className="vista-community-banner__cta-group">
          <Link to="/trips" className="vista-community-banner__btn vista-community-banner__btn--outline">
            Xem 240+ lịch trình chia sẻ
          </Link>
          <Link to="/places" className="vista-community-banner__btn vista-community-banner__btn--primary">
            Khám phá Đà Nẵng ngay
          </Link>
        </div>
      </div>
    </section>
  );
};

export default CommunityBanner;
