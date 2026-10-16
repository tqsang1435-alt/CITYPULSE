import React, { useState, useRef, useEffect } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import "./Header.css";

export interface HeaderUser {
  displayName?: string;
  avatarUrl?: string;
  email?: string;
}

export interface HeaderProps {
  user?: HeaderUser | null;
}

export const Header: React.FC<HeaderProps> = ({ user: propUser }) => {
  const { user: authUser, logout, isAuthenticated } = useAuth();
  const currentUser = authUser || propUser;
  const navigate = useNavigate();
  const location = useLocation();
  const pathname = location.pathname;

  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setDropdownOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const handleLogout = async () => {
    setDropdownOpen(false);
    await logout();
    navigate("/", { replace: true });
  };

  const navItems = [
    { label: "Khám phá", path: "/places" },
    { label: "Lịch trình", path: "/trips" },
    { label: "Sự kiện", path: "/community" },
    { label: "Bản đồ", path: "/places?view=map" },
    { label: "Đã lưu", path: "/saved" },
  ];

  const isNavActive = (path: string) => {
    const basePath = path.split("?")[0];
    if (basePath === "/places" && path.includes("view=map")) {
      return location.pathname === "/places" && location.search.includes("view=map");
    }
    if (basePath === "/places") {
      return location.pathname === "/places" && !location.search.includes("view=map");
    }
    return pathname.startsWith(basePath);
  };

  // Compute 2-letter initials for avatar fallback
  const getInitials = (name?: string) => {
    if (!name) return "VI";
    const parts = name.trim().split(/\s+/);
    if (parts.length === 1) return parts[0].substring(0, 2).toUpperCase();
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  };

  return (
    <header className="dnvibe-header" role="banner">
      <div className="dnvibe-header__inner">
        {/* BRAND LOGO */}
        <Link to="/" className="dnvibe-header__logo-link" aria-label="VISTA Trang chủ">
          <div className="dnvibe-header__logo-icon" aria-hidden="true">
            <svg viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="16" cy="16" r="14" fill="#e2f7ff" />
              <path
                d="M8 18C11 15 14 15 17 18C20 21 23 21 26 18"
                stroke="#006a68"
                strokeWidth="2.5"
                strokeLinecap="round"
              />
              <path
                d="M8 22C11 19 14 19 17 22C20 25 23 25 26 22"
                stroke="#25b7b5"
                strokeWidth="2"
                strokeLinecap="round"
              />
              <circle cx="21" cy="11" r="3" fill="#febe46" />
            </svg>
          </div>
          <div className="dnvibe-header__logo-text-group">
            <span className="dnvibe-header__logo-text">VISTA</span>
            <span className="dnvibe-header__logo-tagline">ĐÀ NẴNG, NHIỀU HƠN MỘT CHUYẾN ĐI</span>
          </div>
        </Link>

        {/* CENTRAL NAVIGATION PILL TRACK (DESKTOP) */}
        <nav className="dnvibe-header__nav" aria-label="Điều hướng chính">
          {navItems.map((item) => {
            const active = isNavActive(item.path);
            return (
              <Link
                key={item.label}
                to={item.path}
                className={`dnvibe-header__nav-item ${active ? "dnvibe-header__nav-item--active" : ""}`}
                aria-current={active ? "page" : undefined}
              >
                {item.label}
              </Link>
            );
          })}
        </nav>

        {/* RIGHT ACTIONS */}
        <div className="dnvibe-header__actions">
          {/* SEARCH TRIGGER */}
          <Link
            to="/places"
            className="dnvibe-header__search-btn"
            aria-label="Tìm kiếm điểm đến tại Đà Nẵng"
            title="Tìm kiếm điểm đến"
          >
            <span className="material-symbols-outlined">search</span>
          </Link>

          {/* WEATHER PILL (DESKTOP / TABLET) */}
          <div className="dnvibe-header__weather-pill" title="Thời tiết hiện tại tại Đà Nẵng">
            <span className="material-symbols-outlined dnvibe-header__weather-icon">wb_sunny</span>
            <span>28°C · Đà Nẵng, VN</span>
          </div>

          {/* USER AVATAR / AUTH TRIGGER */}
          <div className="dnvibe-header__user-wrapper" ref={dropdownRef}>
            {currentUser && isAuthenticated ? (
              <>
                <button
                  type="button"
                  onClick={() => setDropdownOpen((prev) => !prev)}
                  className="dnvibe-header__avatar-btn"
                  aria-expanded={dropdownOpen}
                  aria-haspopup="true"
                  aria-label={`Tài khoản của ${currentUser.displayName || "bạn"}`}
                  title={currentUser.displayName || "Tài khoản cá nhân"}
                >
                  {currentUser.avatarUrl ? (
                    <img
                      src={currentUser.avatarUrl}
                      alt={currentUser.displayName || "Avatar"}
                      className="dnvibe-header__avatar-img"
                    />
                  ) : (
                    <span
                      style={{
                        fontSize: "12px",
                        fontWeight: 700,
                        color: "var(--dnvibe-primary)",
                        letterSpacing: "0.5px",
                      }}
                    >
                      {getInitials(currentUser.displayName)}
                    </span>
                  )}
                  <span className="dnvibe-header__status-dot" aria-hidden="true" />
                </button>

                {/* LOGGED IN DROPDOWN MENU */}
                {dropdownOpen && (
                  <div className="dnvibe-header__dropdown" role="menu">
                    <div className="dnvibe-header__dropdown-info">
                      <div className="dnvibe-header__dropdown-name">
                        {currentUser.displayName || "Người dùng VISTA"}
                      </div>
                      <div className="dnvibe-header__dropdown-email">
                        {currentUser.email || "Đã đăng nhập"}
                      </div>
                    </div>

                    <Link
                      to="/saved"
                      className="dnvibe-header__dropdown-item"
                      role="menuitem"
                      onClick={() => setDropdownOpen(false)}
                    >
                      <span className="material-symbols-outlined text-base">favorite</span>
                      <span>Địa điểm đã lưu</span>
                    </Link>

                    <div className="dnvibe-header__dropdown-divider" aria-hidden="true" />

                    <button
                      type="button"
                      className="dnvibe-header__dropdown-item dnvibe-header__dropdown-item--danger"
                      role="menuitem"
                      onClick={handleLogout}
                    >
                      <span className="material-symbols-outlined text-base">logout</span>
                      <span>Đăng xuất</span>
                    </button>
                  </div>
                )}
              </>
            ) : (
              <Link
                to="/login"
                className="dnvibe-header__avatar-btn"
                aria-label="Đăng nhập vào VISTA"
                title="Đăng nhập VISTA"
              >
                <span className="material-symbols-outlined dnvibe-header__avatar-fallback">
                  person
                </span>
              </Link>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;
