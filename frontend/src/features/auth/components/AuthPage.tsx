import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { ApiError } from "../../../lib/apiClient";
import { login, register } from "../services/authService";

interface Props {
  mode: "login" | "register";
}

export default function AuthPage({ mode }: Props) {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [displayName, setDisplayName] = useState("");
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const isRegister = mode === "register";

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSubmitting(true);
    setError("");
    try {
      if (isRegister) {
        await register(email.trim(), password, displayName.trim());
      } else {
        await login(email.trim(), password);
      }
      navigate("/trips", { replace: true });
    } catch (cause) {
      setError(cause instanceof ApiError ? cause.message : "Không thể kết nối máy chủ.");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="workspace-page">
      <Link to="/" className="wordmark">CITYPULSE®</Link>
      <section className="workspace-card auth-card">
        <p className="eyebrow">YOUR NEXT CHAPTER STARTS HERE</p>
        <h1>{isRegister ? "Tạo tài khoản" : "Chào mừng trở lại"}</h1>
        <p className="workspace-muted">
          {isRegister ? "Đăng ký để tạo chuyến đi và mời bạn bè." : "Đăng nhập để quản lý chuyến đi của bạn."}
        </p>
        <form className="workspace-form" onSubmit={handleSubmit}>
          {isRegister && (
            <label>
              Tên hiển thị
              <input
                value={displayName}
                onChange={(event) => setDisplayName(event.target.value)}
                maxLength={120}
                autoComplete="name"
                required
              />
            </label>
          )}
          <label>
            Email
            <input
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              maxLength={254}
              autoComplete="email"
              required
            />
          </label>
          <label>
            Mật khẩu
            <input
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              minLength={8}
              maxLength={72}
              autoComplete={isRegister ? "new-password" : "current-password"}
              required
            />
          </label>
          {error && <p className="workspace-error" role="alert">{error}</p>}
          <button className="btn btn-dark" type="submit" disabled={submitting}>
            {submitting ? "Đang xử lý…" : isRegister ? "Đăng ký" : "Đăng nhập"}
          </button>
        </form>
        <p className="workspace-switch">
          {isRegister ? "Đã có tài khoản?" : "Chưa có tài khoản?"}{" "}
          <Link to={isRegister ? "/login" : "/register"}>
            {isRegister ? "Đăng nhập" : "Tạo tài khoản"}
          </Link>
        </p>
        <Link className="workspace-back" to="/">← Về trang chủ</Link>
      </section>
    </main>
  );
}
