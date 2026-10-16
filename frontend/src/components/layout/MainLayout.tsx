import React from "react";
import { Link, Outlet } from "react-router-dom";
import Header, { HeaderUser } from "./Header";
import "./MainLayout.css";

export interface MainLayoutProps {
  children?: React.ReactNode;
  user?: HeaderUser | null;
  hideFooter?: boolean;
}

export const MainLayout: React.FC<MainLayoutProps> = ({
  children,
  user = null,
  hideFooter = false,
}) => {
  return (
    <div className="dnvibe-layout">
      <Header user={user} />
      <main className="dnvibe-layout__main">
        {children ?? <Outlet />}
      </main>
      {!hideFooter && (
        <footer className="dnvibe-footer" role="contentinfo">
          <div className="dnvibe-footer__inner">
            <div className="dnvibe-footer__top">
              <div className="dnvibe-footer__brand">
                <div className="dnvibe-footer__logo-row">
                  <span className="material-symbols-outlined text-primary" style={{ color: "var(--dnvibe-primary)" }}>
                    waves
                  </span>
                  <span className="dnvibe-footer__logo-text">VISTA</span>
                </div>
                <p className="dnvibe-footer__desc">
                  Nền tảng du lịch thông minh Đà Nẵng, kết nối bạn với những hành trình ven biển tràn đầy cảm hứng.
                </p>
              </div>

              <nav className="dnvibe-footer__links" aria-label="Liên kết chân trang">
                <Link to="/places" className="dnvibe-footer__link">
                  Khám phá địa điểm
                </Link>
                <Link to="/trips" className="dnvibe-footer__link">
                  Lịch trình mẫu
                </Link>
                <Link to="/places?view=map" className="dnvibe-footer__link">
                  Bản đồ điểm đến
                </Link>
                <Link to="/community" className="dnvibe-footer__link">
                  Cộng đồng du lịch
                </Link>
              </nav>
            </div>

            <div className="dnvibe-footer__bottom">
              <p>© 2026 VISTA · Đà Nẵng, Việt Nam. Lan tỏa cảm hứng biển cả.</p>
              <div className="dnvibe-footer__welcome-tag">
                <span className="material-symbols-outlined text-sm">sailing</span>
                <span>Chào đón bạn tới thành phố biển đáng sống</span>
              </div>
            </div>
          </div>
        </footer>
      )}
    </div>
  );
};

export default MainLayout;
