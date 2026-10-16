import React from "react";
import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

export interface ProtectedRouteProps {
  children?: React.ReactNode;
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children }) => {
  const { isAuthenticated, isLoading } = useAuth();
  const location = useLocation();

  if (isLoading) {
    return (
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          minHeight: "50vh",
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
          Đang xác thực thông tin tài khoản...
        </p>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return children ? <>{children}</> : null;
};

export default ProtectedRoute;
