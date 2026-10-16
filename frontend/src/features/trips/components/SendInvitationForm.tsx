import React, { useState } from "react";
import { sendTripInvitation, InvitationResponse } from "../services/tripService";
import { ApiError } from "../../../lib/apiClient";
import "./SendInvitationForm.css";

interface Props {
  tripId: number;
  tripTitle: string;
  onSuccess?: (invitation: InvitationResponse) => void;
}

type FormStatus =
  | { type: "idle" }
  | { type: "loading" }
  | { type: "success"; invitation: InvitationResponse }
  | { type: "error"; message: string };

/**
 * UC-01: Form gửi lời mời tham gia chuyến đi.
 *
 * Hiển thị: input email, nút gửi, trạng thái loading, thông báo thành công/thất bại.
 */
export default function SendInvitationForm({
  tripId,
  tripTitle,
  onSuccess,
}: Props) {
  const [email, setEmail] = useState("");
  const [status, setStatus] = useState<FormStatus>({ type: "idle" });

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!email.trim()) return;

    setStatus({ type: "loading" });

    try {
      const invitation = await sendTripInvitation(tripId, email.trim());
      setStatus({ type: "success", invitation });
      setEmail("");
      onSuccess?.(invitation);
    } catch (err) {
      if (err instanceof ApiError) {
        setStatus({ type: "error", message: err.message });
      } else {
        setStatus({ type: "error", message: "Đã xảy ra lỗi, vui lòng thử lại." });
      }
    }
  }

  function handleReset() {
    setStatus({ type: "idle" });
    setEmail("");
  }

  const isLoading = status.type === "loading";

  return (
    <div className="inv-form-wrap" aria-label="Gửi lời mời tham gia chuyến đi">
      <div className="inv-form-header">
        <span className="inv-form-tag">03 — TOGETHER</span>
        <h2 className="inv-form-title">Mời thành viên</h2>
        <p className="inv-form-desc">
          Gửi lời mời tham gia chuyến đi{" "}
          <strong>&ldquo;{tripTitle}&rdquo;</strong>
        </p>
      </div>

      {/* Thành công */}
      {status.type === "success" && (
        <div className="inv-feedback inv-feedback--success" role="status">
          <span className="inv-feedback-icon">✓</span>
          <div>
            <p className="inv-feedback-title">Lời mời đã được gửi!</p>
            <p className="inv-feedback-detail">
              Email mời đã gửi đến{" "}
              <strong>{status.invitation.inviteeEmail}</strong>. Lời mời có
              hiệu lực trong 7 ngày.
            </p>
          </div>
          <button
            id="inv-send-another-btn"
            className="inv-btn-text"
            onClick={handleReset}
            type="button"
          >
            Gửi thêm ↗
          </button>
        </div>
      )}

      {/* Form nhập email */}
      {status.type !== "success" && (
        <form
          id="send-invitation-form"
          className="inv-form"
          onSubmit={handleSubmit}
          noValidate
        >
          <div className="inv-field">
            <label htmlFor="inv-email-input" className="inv-label">
              Địa chỉ email
            </label>
            <div className="inv-input-row">
              <input
                id="inv-email-input"
                type="email"
                className="inv-input"
                placeholder="member@example.com"
                value={email}
                onChange={(e) => {
                  setEmail(e.target.value);
                  if (status.type === "error") setStatus({ type: "idle" });
                }}
                disabled={isLoading}
                required
                autoComplete="email"
                aria-describedby={
                  status.type === "error" ? "inv-error-msg" : undefined
                }
                aria-invalid={status.type === "error"}
              />
              <button
                id="inv-submit-btn"
                type="submit"
                className={`inv-btn-primary${isLoading ? " inv-btn-primary--loading" : ""}`}
                disabled={isLoading || !email.trim()}
                aria-busy={isLoading}
              >
                {isLoading ? (
                  <>
                    <span className="inv-spinner" aria-hidden="true" />
                    Đang gửi…
                  </>
                ) : (
                  "Gửi lời mời ↗"
                )}
              </button>
            </div>
          </div>

          {/* Thông báo lỗi */}
          {status.type === "error" && (
            <p
              id="inv-error-msg"
              className="inv-error"
              role="alert"
              aria-live="polite"
            >
              {status.type === "error" && status.message}
            </p>
          )}
        </form>
      )}
    </div>
  );
}
