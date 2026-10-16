import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { apiGet } from "../../lib/apiClient";
import { AdminStatsOverview } from "../../types/api";
import "./AdminDashboardPage.css";

// Dữ liệu mẫu đại diện cho hàng đợi báo cáo (phản ánh từ seed data V8)
const SAMPLE_REPORTS_QUEUE = [
  {
    id: "rep-001",
    target: "Bãi biển Mỹ Khê",
    type: "PLACE",
    reason: "MISINFORMATION",
    desc: "Cần cập nhật giờ ngắm bình minh mùa đông (05:30 thay vì 05:00)",
    status: "OPEN",
    time: "2 giờ trước",
  },
  {
    id: "rep-002",
    target: "Bà Nà Hills & Cầu Vàng",
    type: "PLACE",
    reason: "SPAM",
    desc: "Đối tượng để lại số điện thoại cò mồi xe đưa đón trong phần đánh giá",
    status: "IN_REVIEW",
    time: "1 ngày trước",
  },
  {
    id: "rep-003",
    target: "Cầu Rồng",
    type: "PLACE",
    reason: "INAPPROPRIATE",
    desc: "Thông báo lịch phun lửa tuần này đổi sang 21:15",
    status: "RESOLVED",
    time: "3 ngày trước",
  },
];

export const AdminDashboardPage: React.FC = () => {
  const [stats, setStats] = useState<AdminStatsOverview | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [isUsingFallback, setIsUsingFallback] = useState<boolean>(false);

  useEffect(() => {
    let isMounted = true;

    async function fetchDashboardStats() {
      try {
        const data = await apiGet<AdminStatsOverview>("/admin/dashboard/stats");
        if (isMounted) {
          setStats(data);
          setIsUsingFallback(false);
        }
      } catch {
        // Fallback hiển thị dữ liệu khung Tuần 2 nếu backend chưa bật
        if (isMounted) {
          setStats({
            openReportsCount: 1,
            inReviewReportsCount: 1,
            resolvedReportsCount: 1,
            lockedUsersCount: 1,
            totalModerationActionsCount: 1,
            totalAuditLogsCount: 2,
            systemStatus: "OPERATIONAL (DEMO/SEED)",
          });
          setIsUsingFallback(true);
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    fetchDashboardStats();

    return () => {
      isMounted = false;
    };
  }, []);

  return (
    <div className="admin-dashboard-page">
      {/* Header */}
      <div className="dashboard-header">
        <div className="dashboard-title-group">
          <h1>Bảng Điều Khiển Quản Trị & Kiểm Duyệt</h1>
          <p>Phân hệ Quản trị hệ thống DNVIBE (TV5) — Khung nền tảng Tuần 2</p>
        </div>

        <div className="system-status-badge">
          <span className="status-dot"></span>
          <span>{stats?.systemStatus || "OPERATIONAL"}</span>
          {isUsingFallback && <span style={{ fontSize: "11px", color: "#6b7280" }}>(Dữ liệu mẫu)</span>}
        </div>
      </div>

      {/* KPI Cards Grid */}
      <div className="kpi-grid">
        <div className="kpi-card">
          <div className="kpi-card-header">
            <span className="kpi-title">Báo cáo chờ xử lý</span>
            <div className="kpi-icon-wrap amber">
              <span className="material-symbols-outlined">pending_actions</span>
            </div>
          </div>
          <div className="kpi-value">{isLoading ? "--" : stats?.openReportsCount}</div>
          <div className="kpi-subtext">Cần kiểm duyệt viên tiếp nhận</div>
        </div>

        <div className="kpi-card">
          <div className="kpi-card-header">
            <span className="kpi-title">Đang rà soát</span>
            <div className="kpi-icon-wrap blue">
              <span className="material-symbols-outlined">hourglass_top</span>
            </div>
          </div>
          <div className="kpi-value">{isLoading ? "--" : stats?.inReviewReportsCount}</div>
          <div className="kpi-subtext">Đã được gán cho Moderator</div>
        </div>

        <div className="kpi-card">
          <div className="kpi-card-header">
            <span className="kpi-title">Đã giải quyết</span>
            <div className="kpi-icon-wrap green">
              <span className="material-symbols-outlined">task_alt</span>
            </div>
          </div>
          <div className="kpi-value">{isLoading ? "--" : stats?.resolvedReportsCount}</div>
          <div className="kpi-subtext">Báo cáo đã xử lý xong</div>
        </div>

        <div className="kpi-card">
          <div className="kpi-card-header">
            <span className="kpi-title">Tài khoản bị khóa</span>
            <div className="kpi-icon-wrap red">
              <span className="material-symbols-outlined">lock_person</span>
            </div>
          </div>
          <div className="kpi-value">{isLoading ? "--" : stats?.lockedUsersCount}</div>
          <div className="kpi-subtext">Tài khoản vi phạm (LOCKED)</div>
        </div>

        <div className="kpi-card">
          <div className="kpi-card-header">
            <span className="kpi-title">Thao tác kiểm duyệt</span>
            <div className="kpi-icon-wrap teal">
              <span className="material-symbols-outlined">verified</span>
            </div>
          </div>
          <div className="kpi-value">{isLoading ? "--" : stats?.totalModerationActionsCount}</div>
          <div className="kpi-subtext">Quyết định duyệt / ẩn / khôi phục</div>
        </div>

        <div className="kpi-card">
          <div className="kpi-card-header">
            <span className="kpi-title">Nhật ký kiểm toán</span>
            <div className="kpi-icon-wrap purple">
              <span className="material-symbols-outlined">security</span>
            </div>
          </div>
          <div className="kpi-value">{isLoading ? "--" : stats?.totalAuditLogsCount}</div>
          <div className="kpi-subtext">Bản ghi Audit Log bất biến</div>
        </div>
      </div>

      {/* Quick Action Shortcuts */}
      <div className="admin-section">
        <h3 className="section-title">
          <span className="material-symbols-outlined">bolt</span>
          <span>Truy Cập Nhanh Các Phân Hệ</span>
        </h3>

        <div className="quick-actions-grid">
          <Link to="/admin/reports" className="action-card">
            <div className="action-icon">
              <span className="material-symbols-outlined">flag</span>
            </div>
            <div className="action-info">
              <h4>Hàng Đợi Báo Cáo</h4>
              <p>Xem và tiếp nhận các báo cáo vi phạm nội dung</p>
            </div>
          </Link>

          <Link to="/admin/moderation" className="action-card">
            <div className="action-icon">
              <span className="material-symbols-outlined">verified_user</span>
            </div>
            <div className="action-info">
              <h4>Kiểm Duyệt Nội Dung</h4>
              <p>Xem xét duyệt địa điểm đề xuất, đánh giá và bài viết</p>
            </div>
          </Link>

          <Link to="/admin/audit-logs" className="action-card">
            <div className="action-icon">
              <span className="material-symbols-outlined">history</span>
            </div>
            <div className="action-info">
              <h4>Nhật Ký Kiểm Toán</h4>
              <p>Truy vết ai đã làm gì, vào thời điểm nào (Audit Log)</p>
            </div>
          </Link>

          <Link to="/admin/users" className="action-card">
            <div className="action-icon">
              <span className="material-symbols-outlined">manage_accounts</span>
            </div>
            <div className="action-info">
              <h4>Quản Trị Người Dùng</h4>
              <p>Khóa/mở khóa tài khoản và phân quyền hệ thống</p>
            </div>
          </Link>
        </div>
      </div>

      {/* Recent Queue Preview Table */}
      <div className="admin-section">
        <h3 className="section-title">
          <span className="material-symbols-outlined">list_alt</span>
          <span>Hàng Đợi Báo Cáo Mới Nhất (Dữ Liệu Mẫu Đại Diện)</span>
        </h3>

        <div className="table-responsive">
          <table className="admin-table">
            <thead>
              <tr>
                <th>Mã Báo Cáo</th>
                <th>Đối Tượng</th>
                <th>Phân Loại</th>
                <th>Lý Do</th>
                <th>Trạng Thái</th>
                <th>Thời Gian</th>
              </tr>
            </thead>
            <tbody>
              {SAMPLE_REPORTS_QUEUE.map((item) => (
                <tr key={item.id}>
                  <td>
                    <code style={{ color: "#006a68", fontWeight: 600 }}>{item.id}</code>
                  </td>
                  <td><strong>{item.target}</strong></td>
                  <td>
                    <span style={{ fontSize: "12px", background: "#f1f5f9", padding: "2px 6px", borderRadius: "4px" }}>
                      {item.type}
                    </span>
                  </td>
                  <td>
                    <div style={{ fontWeight: 600, color: "#1e293b" }}>{item.reason}</div>
                    <div style={{ fontSize: "12px", color: "#64748b" }}>{item.desc}</div>
                  </td>
                  <td>
                    <span className={`status-badge-inline ${item.status.toLowerCase().replace("_", "-")}`}>
                      {item.status}
                    </span>
                  </td>
                  <td style={{ color: "#64748b", fontSize: "13px" }}>{item.time}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="admin-callout">
          <strong>Thông báo tiến độ Tuần 2:</strong> Khung Admin đã được kết nối với cơ sở dữ liệu và Spring Security RBAC. Các chức năng duyệt/từ chối chi tiết và giao diện xử lý hàng đợi hoàn chỉnh sẽ được tiếp tục triển khai theo kế hoạch của Tuần 4 & Tuần 5.
        </div>
      </div>
    </div>
  );
};

export default AdminDashboardPage;
