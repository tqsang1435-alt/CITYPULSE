import React from "react";
import "./DiscoverHeader.css";

export const DiscoverHeader: React.FC = () => {
  return (
    <div className="vista-discover-header">
      {/* BACKGROUND ATMOSPHERE WITH SKYLINE SILHOUETTE */}
      <div className="vista-discover-header__bg" aria-hidden="true">
        <div className="vista-discover-header__skyline" />
      </div>

      <div className="dnvibe-container vista-discover-header__inner">
        <div className="vista-discover-header__left">
          <div className="vista-discover-header__title-row">
            <h1 className="vista-discover-header__title">
              Khám phá Đà Nẵng
              <span className="vista-discover-header__sunbeams" aria-hidden="true">
                <svg width="28" height="28" viewBox="0 0 28 28" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M14 2V6" stroke="#FEBE46" strokeWidth="2.5" strokeLinecap="round" />
                  <path d="M22 6L19 9" stroke="#FEBE46" strokeWidth="2.5" strokeLinecap="round" />
                  <path d="M26 14H22" stroke="#FEBE46" strokeWidth="2.5" strokeLinecap="round" />
                </svg>
              </span>
            </h1>
          </div>
          <p className="vista-discover-header__subtitle">
            Tìm những địa điểm thú vị, trải nghiệm đặc sắc và góc nhìn mới về thành phố biển xinh đẹp.
          </p>
        </div>

        {/* RIGHT DECORATIVE HANDWRITTEN NOTE */}
        <div className="vista-discover-header__right" aria-hidden="true">
          <div className="vista-discover-header__note-bubble">
            <span>Một thành phố ngập tràn trải nghiệm ♡</span>
          </div>
          <svg className="vista-discover-header__arrow" width="36" height="36" viewBox="0 0 36 36" fill="none">
            <path
              d="M10 8 C 18 16, 24 18, 28 26"
              stroke="#6c7a79"
              strokeWidth="1.8"
              strokeDasharray="3 3"
              strokeLinecap="round"
            />
            <path
              d="M22 26 L 28 26 L 28 20"
              stroke="#6c7a79"
              strokeWidth="1.8"
              strokeLinecap="round"
              strokeLinejoin="round"
            />
          </svg>
        </div>
      </div>
    </div>
  );
};

export default DiscoverHeader;
