import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { ApiClientError } from "../lib/apiClient";
import "./LoginPage.css";

export const RegisterPage: React.FC = () => {
  const { register } = useAuth();
  const navigate = useNavigate();

  const [displayName, setDisplayName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMsg(null);

    // Client Validation
    const nameTrimmed = displayName.trim();
    const emailTrimmed = email.trim();

    if (!nameTrimmed) {
      setErrorMsg("Vui lòng nhập họ và tên của bạn.");
      return;
    }

    if (!emailTrimmed) {
      setErrorMsg("Vui lòng nhập địa chỉ email.");
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(emailTrimmed)) {
      setErrorMsg("Định dạng email không hợp lệ. Ví dụ: ban@example.com");
      return;
    }

    if (password.length < 6) {
      setErrorMsg("Mật khẩu phải có ít nhất 6 ký tự.");
      return;
    }

    if (password !== confirmPassword) {
      setErrorMsg("Mật khẩu xác nhận không khớp.");
      return;
    }

    setIsSubmitting(true);

    try {
      await register(nameTrimmed, emailTrimmed, password);
      // Auto-login succeeds, navigate to home
      navigate("/", { replace: true });
    } catch (err: unknown) {
      if (err instanceof ApiClientError) {
        if (err.status === 409) {
          setErrorMsg("Email này đã được đăng ký. Vui lòng sử dụng email khác hoặc đăng nhập.");
        } else if (err.data?.message) {
          setErrorMsg(err.data.message);
        } else {
          setErrorMsg(err.message || "Đăng ký không thành công.");
        }
      } else {
        setErrorMsg("Có lỗi xảy ra trong quá trình đăng ký. Vui lòng thử lại.");
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="vista-auth-container">
      <div className="vista-auth-card">
        <div className="vista-auth-card__header">
          <div className="vista-auth-card__badge">
            <span className="material-symbols-outlined text-sm">person_add</span>
            <span>Tạo tài khoản mới</span>
          </div>
          <h1 className="vista-auth-card__title">Đăng ký VISTA</h1>
          <p className="vista-auth-card__subtitle">
            Cùng hàng nghìn du khách khám phá vẻ đẹp bất tận của thành phố biển Đà Nẵng
          </p>
        </div>

        {errorMsg && (
          <div className="vista-auth-card__alert vista-auth-card__alert--error" role="alert">
            <span className="material-symbols-outlined text-base">error</span>
            <span>{errorMsg}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="vista-auth-form" noValidate>
          {/* DISPLAY NAME */}
          <div className="vista-auth-field">
            <label htmlFor="reg-name" className="vista-auth-field__label">
              Họ và tên
            </label>
            <div className="vista-auth-field__input-wrap">
              <span className="material-symbols-outlined vista-auth-field__icon">badge</span>
              <input
                id="reg-name"
                type="text"
                autoComplete="name"
                required
                disabled={isSubmitting}
                placeholder="Ví dụ: Nguyễn Văn An"
                value={displayName}
                onChange={(e) => setDisplayName(e.target.value)}
                className="vista-auth-field__input"
              />
            </div>
          </div>

          {/* EMAIL */}
          <div className="vista-auth-field">
            <label htmlFor="reg-email" className="vista-auth-field__label">
              Địa chỉ Email
            </label>
            <div className="vista-auth-field__input-wrap">
              <span className="material-symbols-outlined vista-auth-field__icon">mail</span>
              <input
                id="reg-email"
                type="email"
                autoComplete="email"
                required
                disabled={isSubmitting}
                placeholder="name@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="vista-auth-field__input"
              />
            </div>
          </div>

          {/* PASSWORD */}
          <div className="vista-auth-field">
            <label htmlFor="reg-password" className="vista-auth-field__label">
              Mật khẩu (tối thiểu 6 ký tự)
            </label>
            <div className="vista-auth-field__input-wrap">
              <span className="material-symbols-outlined vista-auth-field__icon">key</span>
              <input
                id="reg-password"
                type="password"
                autoComplete="new-password"
                required
                disabled={isSubmitting}
                placeholder="Nhập mật khẩu an toàn"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="vista-auth-field__input"
              />
            </div>
          </div>

          {/* CONFIRM PASSWORD */}
          <div className="vista-auth-field">
            <label htmlFor="reg-confirm-password" className="vista-auth-field__label">
              Xác nhận lại mật khẩu
            </label>
            <div className="vista-auth-field__input-wrap">
              <span className="material-symbols-outlined vista-auth-field__icon">check_circle</span>
              <input
                id="reg-confirm-password"
                type="password"
                autoComplete="new-password"
                required
                disabled={isSubmitting}
                placeholder="Nhập lại mật khẩu phía trên"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                className="vista-auth-field__input"
              />
            </div>
          </div>

          {/* SUBMIT BUTTON */}
          <button
            type="submit"
            disabled={isSubmitting}
            className="vista-auth-card__submit-btn"
          >
            {isSubmitting ? (
              <>
                <span
                  className="material-symbols-outlined text-base"
                  style={{ animation: "spin 1s linear infinite" }}
                >
                  progress_activity
                </span>
                <span>Đang thiết lập tài khoản...</span>
              </>
            ) : (
              <>
                <span>Hoàn tất đăng ký</span>
                <span className="material-symbols-outlined text-base">arrow_forward</span>
              </>
            )}
          </button>
        </form>

        <div className="vista-auth-card__footer">
          <span>Đã có tài khoản VISTA?</span>
          <Link to="/login" className="vista-auth-card__link">
            Đăng nhập ngay
          </Link>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;
