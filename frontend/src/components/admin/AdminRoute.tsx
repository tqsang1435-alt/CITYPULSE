import React from "react";
import { Link, Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import Button from "../common/Button";

export interface AdminRouteProps {
  children?: React.ReactNode;
}

const ALLOWED_ADMIN_ROLES = ["ADMIN", "CONTENT_ADMIN", "SYSTEM_ADMIN"];

export const AdminRoute: React.FC<AdminRouteProps> = ({ children }) => {
  const { user, isAuthenticated, isLoading, logout } = useAuth();
  const location = useLocation();

  if (isLoading) {
    return (
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          minHeight: "100vh",
          gap: "16px",
          color: "var(--dnvibe-primary)",
        }}
      >
        <span
          className="material-symbols-outlined"
          style={{ fontSize: "40px", animation: "spin 1s linear infinite" }}
        >
          progress_activity
        </span>
        <p style={{ fontSize: "15px", color: "var(--dnvibe-neutral-600)" }}>
          Đang xác thực thông tin quyền quản trị...
        </p>
      </div>
    );
  }

  // 1. Not logged in -> Redirect to login
  if (!isAuthenticated || !user) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // 2. Logged in but not an administrative role -> 403 Forbidden screen
  if (!ALLOWED_ADMIN_ROLES.includes(user.role)) {
    return (
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          minHeight: "80vh",
          padding: "24px",
          textAlign: "center",
          gap: "16px",
        }}
      >
        <div
          style={{
            width: "64px",
            height: "64px",
            borderRadius: "50%",
            background: "#fee2e2",
            color: "#dc2626",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            fontSize: "32px",
          }}
        >
          <span className="material-symbols-outlined" style={{ fontSize: "36px" }}>
            gpp_bad
          </span>
        </div>
        <h1 style={{ fontSize: "24px", fontWeight: 700, color: "#111827" }}>
          403 — Không có quyền truy cập
        </h1>
        <p style={{ maxWidth: "480px", color: "#4b5563", lineHeight: 1.6 }}>
          Tài khoản <strong>{user.email}</strong> chỉ có vai trò{" "}
          <code style={{ background: "#f3f4f6", padding: "2px 6px", borderRadius: "4px" }}>
            {user.role}
          </code>
          . Bạn không có quyền truy cập vào phân hệ Quản trị & Kiểm duyệt của DNVIBE.
        </p>
        <div style={{ display: "flex", gap: "12px", marginTop: "8px" }}>
          <Link to="/" style={{ textDecoration: "none" }}>
            <Button variant="primary" size="md">
              ← Về trang khám phá
            </Button>
          </Link>
          <Button variant="secondary" size="md" onClick={() => logout()}>
            Đăng xuất tài khoản
          </Button>
        </div>
      </div>
    );
  }

  // 3. Authenticated with Admin role -> Render protected admin children
  return children ? <>{children}</> : null;
};

export default AdminRoute;
