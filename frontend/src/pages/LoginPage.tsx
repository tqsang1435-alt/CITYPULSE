import React, { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { ApiClientError } from "../lib/apiClient";
import "./LoginPage.css";

export const LoginPage: React.FC = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Read target return URL from location state if user was redirected from a protected route
  const fromLocation = (location.state as { from?: { pathname?: string } })?.from?.pathname || "/";

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email.trim() || !password) {
      setErrorMsg("Vui lòng điền đầy đủ email và mật khẩu.");
      return;
    }

    setIsSubmitting(true);
    setErrorMsg(null);

    try {
      await login(email.trim(), password);
      // Success: redirect to target or home
      navigate(fromLocation, { replace: true });
    } catch (err: unknown) {
      if (err instanceof ApiClientError) {
        if (err.status === 401) {
          setErrorMsg("Email hoặc mật khẩu không chính xác.");
        } else if (err.status === 403) {
          setErrorMsg("Tài khoản của bạn đang bị khóa.");
        } else if (err.data?.message) {
          setErrorMsg(err.data.message);
        } else {
          setErrorMsg(err.message || "Đăng nhập không thành công.");
        }
      } else {
        setErrorMsg("Có lỗi xảy ra trong quá trình đăng nhập. Vui lòng thử lại.");
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
            <span className="material-symbols-outlined text-sm">lock</span>
            <span>VISTA Account</span>
          </div>
          <h1 className="vista-auth-card__title">Đăng nhập</h1>
          <p className="vista-auth-card__subtitle">
            Chào mừng bạn trở lại với VISTA — Nền tảng du lịch thông minh Đà Nẵng
          </p>
        </div>

        {errorMsg && (
          <div className="vista-auth-card__alert vista-auth-card__alert--error" role="alert">
            <span className="material-symbols-outlined text-base">error</span>
            <span>{errorMsg}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="vista-auth-form" noValidate>
          {/* EMAIL FIELD */}
          <div className="vista-auth-field">
            <label htmlFor="login-email" className="vista-auth-field__label">
              Địa chỉ Email
            </label>
            <div className="vista-auth-field__input-wrap">
              <span className="material-symbols-outlined vista-auth-field__icon">mail</span>
              <input
                id="login-email"
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

          {/* PASSWORD FIELD */}
          <div className="vista-auth-field">
            <label htmlFor="login-password" className="vista-auth-field__label">
              Mật khẩu
            </label>
            <div className="vista-auth-field__input-wrap">
              <span className="material-symbols-outlined vista-auth-field__icon">key</span>
              <input
                id="login-password"
                type="password"
                autoComplete="current-password"
                required
                disabled={isSubmitting}
                placeholder="Nhập mật khẩu của bạn"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="vista-auth-field__input"
              />
            </div>
          </div>

          {/* FORGOT PASSWORD HINT */}
          <div className="vista-auth-card__options">
            <button
              type="button"
              className="vista-auth-card__forgot-btn"
              title="Tính năng quên mật khẩu đang được triển khai"
            >
              Quên mật khẩu? (Sắp có)
            </button>
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
                <span>Đang xử lý đăng nhập...</span>
              </>
            ) : (
              <>
                <span>Đăng nhập</span>
                <span className="material-symbols-outlined text-base">arrow_forward</span>
              </>
            )}
          </button>
        </form>

        <div className="vista-auth-card__footer">
          <span>Chưa có tài khoản VISTA?</span>
          <Link to="/register" className="vista-auth-card__link">
            Đăng ký ngay
          </Link>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
