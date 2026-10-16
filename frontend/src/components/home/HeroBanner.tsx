import React from "react";
import PlannerBar, { PlannerValues } from "./PlannerBar";
import "./HeroBanner.css";

export interface HeroBannerProps {
  onPlanSubmit?: (values: PlannerValues) => void;
}

export const HeroBanner: React.FC<HeroBannerProps> = ({ onPlanSubmit }) => {
  return (
    <section className="vista-hero">
      {/* BACKGROUND IMAGE & ATMOSPHERIC COASTAL OVERLAYS */}
      <div className="vista-hero__bg-container">
        <img
          src="/images/my-khe.png"
          alt="Toàn cảnh bãi biển Mỹ Khê Đà Nẵng nắng sớm trong lành"
          className="vista-hero__bg-image"
        />
        {/* Soft coastal aqua & sunlight gradients */}
        <div className="vista-hero__scrim-gradient" />
        <div className="vista-hero__side-gradient" />
      </div>

      {/* HERO FOREGROUND CONTENT */}
      <div className="vista-hero__content dnvibe-container">
        {/* LIVE CITY PULSE BADGE */}
        <div className="vista-hero__pulse-badge">
          <span className="vista-hero__pulse-outer" />
          <span className="vista-hero__pulse-inner" />
          <span className="vista-hero__pulse-text">Hôm nay 28°C • Biển êm • Sóng nhẹ 0.4m</span>
        </div>

        {/* MAIN EDITORIAL HEADLINE */}
        <h1 className="vista-hero__headline">
          Bạn có <span className="vista-hero__highlight">4 tiếng</span> ở Đà Nẵng?
        </h1>

        {/* SUBHEADLINE */}
        <p className="vista-hero__subheadline">
          Để <strong className="vista-hero__brand-tag">VISTA</strong> giúp bạn tạo lịch trình thông minh,
          trọn vẹn và đúng chất Đà Nẵng theo thời gian thực.
        </p>

        {/* SUBTLE COASTAL NOTES */}
        <div className="vista-hero__vibes-pills" aria-hidden="true">
          <span className="vista-hero__vibe-tag">🌊 Biển xanh</span>
          <span className="vista-hero__vibe-dot">•</span>
          <span className="vista-hero__vibe-tag">✨ Người thân thiện</span>
          <span className="vista-hero__vibe-dot">•</span>
          <span className="vista-hero__vibe-tag">🌴 Trải nghiệm thật</span>
        </div>

        {/* FLOATING SMART PLANNER BAR */}
        <div className="vista-hero__planner-wrapper">
          <PlannerBar onPlanSubmit={onPlanSubmit} />
        </div>
      </div>
    </section>
  );
};

export default HeroBanner;
