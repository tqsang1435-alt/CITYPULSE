import React, { useState } from "react";
import "./PlannerBar.css";

export interface PlannerValues {
  time: string;
  budget: string;
  interest: string;
  transport: string;
}

export interface PlannerBarProps {
  onPlanSubmit?: (values: PlannerValues) => void;
}

export const PlannerBar: React.FC<PlannerBarProps> = ({ onPlanSubmit }) => {
  const [time, setTime] = useState("4h");
  const [budget, setBudget] = useState("all");
  const [interest, setInterest] = useState("food_checkin");
  const [transport, setTransport] = useState("all");
  const [isSubmitted, setIsSubmitted] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitted(true);
    if (onPlanSubmit) {
      onPlanSubmit({ time, budget, interest, transport });
    }
    setTimeout(() => setIsSubmitted(false), 2500);
  };

  return (
    <div className="vista-planner">
      <form onSubmit={handleSubmit} className="vista-planner__form">
        {/* 4 INPUT FIELDS GRID */}
        <div className="vista-planner__fields-grid">
          {/* FIELD 1: THỜI GIAN */}
          <div className="vista-planner__field">
            <div className="vista-planner__field-header">
              <span className="material-symbols-outlined vista-planner__field-icon">schedule</span>
              <label htmlFor="planner-time" className="vista-planner__field-label">
                Thời gian
              </label>
            </div>
            <div className="vista-planner__select-wrapper">
              <select
                id="planner-time"
                value={time}
                onChange={(e) => setTime(e.target.value)}
                className="vista-planner__select"
              >
                <option value="4h">4 tiếng (Dạo nhanh)</option>
                <option value="8h">8 tiếng (Nửa ngày)</option>
                <option value="1d">1 ngày (Trọn vẹn)</option>
                <option value="2d1n">2 ngày 1 đêm</option>
                <option value="3d2n">3 ngày 2 đêm</option>
              </select>
              <span className="material-symbols-outlined vista-planner__arrow-icon" aria-hidden="true">
                expand_more
              </span>
            </div>
          </div>

          {/* FIELD 2: NGÂN SÁCH */}
          <div className="vista-planner__field">
            <div className="vista-planner__field-header">
              <span className="material-symbols-outlined vista-planner__field-icon">payments</span>
              <label htmlFor="planner-budget" className="vista-planner__field-label">
                Ngân sách
              </label>
            </div>
            <div className="vista-planner__select-wrapper">
              <select
                id="planner-budget"
                value={budget}
                onChange={(e) => setBudget(e.target.value)}
                className="vista-planner__select"
              >
                <option value="all">Mọi mức</option>
                <option value="saving">Tiết kiệm (&lt; 300k)</option>
                <option value="medium">Vừa phải (300k - 700k)</option>
                <option value="luxury">Thoải mái (&gt; 700k)</option>
              </select>
              <span className="material-symbols-outlined vista-planner__arrow-icon" aria-hidden="true">
                expand_more
              </span>
            </div>
          </div>

          {/* FIELD 3: SỞ THÍCH */}
          <div className="vista-planner__field">
            <div className="vista-planner__field-header">
              <span className="material-symbols-outlined vista-planner__field-icon">surfing</span>
              <label htmlFor="planner-interest" className="vista-planner__field-label">
                Sở thích
              </label>
            </div>
            <div className="vista-planner__select-wrapper">
              <select
                id="planner-interest"
                value={interest}
                onChange={(e) => setInterest(e.target.value)}
                className="vista-planner__select"
              >
                <option value="food_checkin">Ăn uống, Check-in</option>
                <option value="beach">Biển & Chill thư giãn</option>
                <option value="food">Ẩm thực địa phương</option>
                <option value="culture">Văn hóa & Di sản</option>
              </select>
              <span className="material-symbols-outlined vista-planner__arrow-icon" aria-hidden="true">
                expand_more
              </span>
            </div>
          </div>

          {/* FIELD 4: PHƯƠNG TIỆN */}
          <div className="vista-planner__field">
            <div className="vista-planner__field-header">
              <span className="material-symbols-outlined vista-planner__field-icon">two_wheeler</span>
              <label htmlFor="planner-transport" className="vista-planner__field-label">
                Phương tiện
              </label>
            </div>
            <div className="vista-planner__select-wrapper">
              <select
                id="planner-transport"
                value={transport}
                onChange={(e) => setTransport(e.target.value)}
                className="vista-planner__select"
              >
                <option value="all">Tất cả</option>
                <option value="bike">Xe máy linh hoạt</option>
                <option value="taxi">Taxi / Grab tiện lợi</option>
                <option value="walk">Đi bộ tản bộ</option>
              </select>
              <span className="material-symbols-outlined vista-planner__arrow-icon" aria-hidden="true">
                expand_more
              </span>
            </div>
          </div>
        </div>

        {/* BOTTOM CTA BAR */}
        <div className="vista-planner__bottom-bar">
          <div className="vista-planner__ai-hint">
            <span
              className="material-symbols-outlined vista-planner__ai-icon"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              auto_awesome
            </span>
            <span>Hệ thống AI tự động tối ưu đường đi & tránh khung giờ kẹt xe</span>
          </div>

          <button
            type="submit"
            className="vista-planner__submit-btn"
            title="Tạo lịch trình du lịch thông minh"
          >
            <span>{isSubmitted ? "Đang tạo lịch trình..." : "Tạo lịch trình ngay"}</span>
            <span className="material-symbols-outlined vista-planner__submit-icon">waves</span>
          </button>
        </div>
      </form>
    </div>
  );
};

export default PlannerBar;
