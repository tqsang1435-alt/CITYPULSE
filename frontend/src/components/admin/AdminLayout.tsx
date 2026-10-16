import React from "react";
import { Link, NavLink, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import "./AdminLayout.css";

export const AdminLayout: React.FC = () => {
  const { user, logout } = useAuth();
  const location = useLocation();

  const getPageTitle = () => {
    const path = location.pathname;
    if (path === "/admin" || path === "/admin/") return "Tổng quan hệ thống";
    if (path.startsWith("/admin/reports")) return "Quản lý Báo cáo vi phạm";
    if (path.startsWith("/admin/moderation")) return "Hàng đợi Kiểm duyệt nội dung";
    if (path.startsWith("/admin/places")) return "Quản lý Địa điểm chính thức";
    if (path.startsWith("/admin/users")) return "Quản lý Tài khoản & Phân quyền";
    if (path.startsWith("/admin/audit-logs")) return "Nhật ký Kiểm toán hệ thống";
    return "Phân hệ Quản trị";
  };

  return (
    <div className="admin-container">
      {/* Sidebar */}
      <aside className="admin-sidebar">
        <div className="admin-sidebar-header">
          <Link to="/admin" className="admin-logo">
            <div className="admin-logo-icon">V</div>
            <span className="admin-logo-text">DNVIBE</span>
          </Link>
          <span className="admin-badge">Admin</span>
        </div>

        <nav className="admin-nav">
          <NavLink
            to="/admin"
            end
            className={({ isActive }) => `admin-nav-item ${isActive ? "active" : ""}`}
          >
            <span className="material-symbols-outlined">dashboard</span>
            <span>Tổng quan</span>
          </NavLink>

          <NavLink
            to="/admin/reports"
            className={({ isActive }) => `admin-nav-item ${isActive ? "active" : ""}`}
          >
            <span className="material-symbols-outlined">flag</span>
            <span>Báo cáo vi phạm</span>
          </NavLink>

          <NavLink
            to="/admin/moderation"
            className={({ isActive }) => `admin-nav-item ${isActive ? "active" : ""}`}
          >
            <span className="material-symbols-outlined">verified_user</span>
            <span>Hàng đợi kiểm duyệt</span>
          </NavLink>

          <NavLink
            to="/admin/places"
            className={({ isActive }) => `admin-nav-item ${isActive ? "active" : ""}`}
          >
            <span className="material-symbols-outlined">location_on</span>
            <span>Quản lý địa điểm</span>
          </NavLink>

          <NavLink
            to="/admin/users"
            className={({ isActive }) => `admin-nav-item ${isActive ? "active" : ""}`}
          >
            <span className="material-symbols-outlined">manage_accounts</span>
            <span>Quản lý tài khoản</span>
          </NavLink>

          <NavLink
            to="/admin/audit-logs"
            className={({ isActive }) => `admin-nav-item ${isActive ? "active" : ""}`}
          >
            <span className="material-symbols-outlined">history</span>
            <span>Nhật ký kiểm toán</span>
          </NavLink>
        </nav>

        <div className="admin-sidebar-footer">
          <Link to="/" className="admin-back-link">
            <span className="material-symbols-outlined" style={{ fontSize: "18px" }}>
              arrow_back
            </span>
            <span>Về trang khám phá</span>
          </Link>
        </div>
      </aside>

      {/* Main Area */}
      <div className="admin-main">
        {/* Topbar */}
        <header className="admin-topbar">
          <div className="admin-topbar-left">
            <div className="admin-breadcrumb">
              <span>Admin</span>
              <span>/</span>
              <span className="admin-breadcrumb-current">{getPageTitle()}</span>
            </div>
          </div>

          <div className="admin-topbar-right">
            <div className="admin-user-pill">
              <div className="admin-avatar">
                {user?.displayName ? user.displayName.charAt(0).toUpperCase() : "A"}
              </div>
              <div className="admin-user-info">
                <span className="admin-user-name">{user?.displayName || "Quản trị viên"}</span>
                <span className="admin-user-role">{user?.role || "ADMIN"}</span>
              </div>
            </div>

            <button className="admin-logout-btn" onClick={() => logout()} title="Đăng xuất">
              <span className="material-symbols-outlined" style={{ fontSize: "18px" }}>
                logout
              </span>
              <span>Đăng xuất</span>
            </button>
          </div>
        </header>

        {/* Page Content */}
        <main className="admin-content-body">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default AdminLayout;
