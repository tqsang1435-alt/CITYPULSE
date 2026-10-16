import React from "react";
import { Link } from "react-router-dom";
import Button from "../../components/common/Button";

interface AdminPlaceholderPageProps {
  title: string;
  description: string;
  scheduledWeek: string;
}

export const AdminPlaceholderPage: React.FC<AdminPlaceholderPageProps> = ({
  title,
  description,
  scheduledWeek,
}) => {
  return (
    <div
      style={{
        background: "#ffffff",
        borderRadius: "12px",
        padding: "48px 24px",
        textAlign: "center",
        border: "1px solid #e2e8f0",
        boxShadow: "0 1px 3px rgba(0,0,0,0.04)",
      }}
    >
      <div
        style={{
          width: "56px",
          height: "56px",
          borderRadius: "12px",
          background: "rgba(0, 106, 104, 0.1)",
          color: "var(--dnvibe-primary)",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          margin: "0 auto 16px auto",
        }}
      >
        <span className="material-symbols-outlined" style={{ fontSize: "32px" }}>
          construction
        </span>
      </div>

      <span
        style={{
          fontSize: "12px",
          fontWeight: 700,
          color: "var(--dnvibe-primary)",
          textTransform: "uppercase",
          letterSpacing: "0.5px",
        }}
      >
        Khung phân hệ TV5 — {scheduledWeek}
      </span>

      <h2 style={{ fontSize: "22px", fontWeight: 700, color: "#0f172a", margin: "8px 0" }}>
        {title}
      </h2>

      <p style={{ maxWidth: "540px", margin: "0 auto 24px auto", color: "#64748b", lineHeight: 1.6 }}>
        {description}
      </p>

      <div style={{ display: "flex", gap: "12px", justifyContent: "center" }}>
        <Link to="/admin" style={{ textDecoration: "none" }}>
          <Button variant="primary" size="md">
            ← Về Dashboard Tổng Quan
          </Button>
        </Link>
      </div>
    </div>
  );
};

export const AdminReportsPage: React.FC = () => (
  <AdminPlaceholderPage
    title="Quản Lý Hàng Đợi Báo Cáo Vi Phạm"
    description="Giao diện chi tiết duyệt và xử lý các báo cáo vi phạm (Resolve, Reject, gộp trùng lặp). Đang ở trạng thái khung nền tảng Tuần 2, sẽ được lập trình logic nghiệp vụ đầy đủ trong Tuần 4."
    scheduledWeek="Lập trình chi tiết tại Tuần 4"
  />
);

export const AdminModerationPage: React.FC = () => (
  <AdminPlaceholderPage
    title="Hàng Đợi Kiểm Duyệt Nội Dung"
    description="Giao diện tiền kiểm địa điểm đề xuất (Submission) và hậu kiểm đánh giá (Review/Post). Sẽ được hoàn thiện API và UI trong Tuần 4 & Tuần 5."
    scheduledWeek="Lập trình chi tiết tại Tuần 4 & 5"
  />
);

export const AdminPlacesPage: React.FC = () => (
  <AdminPlaceholderPage
    title="Quản Lý Địa Điểm Chính Thức"
    description="Công cụ dành cho Content Admin chỉnh sửa thông tin, ẩn (archive) và khôi phục địa điểm chính thức của Đà Nẵng."
    scheduledWeek="Lập trình chi tiết tại Tuần 5"
  />
);

export const AdminUsersPage: React.FC = () => (
  <AdminPlaceholderPage
    title="Quản Lý Tài Khoản & Phân Quyền"
    description="Bảng điều khiển dành cho System Admin quản lý khóa/mở khóa tài khoản người dùng vi phạm và điều chỉnh quyền hạn."
    scheduledWeek="Lập trình chi tiết tại Tuần 5"
  />
);

export const AdminAuditLogsPage: React.FC = () => (
  <AdminPlaceholderPage
    title="Tra Cứu Nhật Ký Kiểm Toán (Audit Logs)"
    description="Công cụ truy vết lịch sử thao tác của các quản trị viên. Dữ liệu được lưu bất biến (append-only) trong bảng audit_logs của PostgreSQL."
    scheduledWeek="Lập trình chi tiết tại Tuần 5"
  />
);
