import { FormEvent, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { ApiError, getAuthToken } from "../../../lib/apiClient";
import { createTrip, listTrips, TripSummary } from "../services/tripService";
import SendInvitationForm from "./SendInvitationForm";

export default function TripsPage() {
  const [trips, setTrips] = useState<TripSummary[]>([]);
  const [title, setTitle] = useState("");
  const [selectedTripId, setSelectedTripId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const authenticated = Boolean(getAuthToken());

  async function refreshTrips() {
    const result = await listTrips();
    setTrips(result);
    setSelectedTripId((current) => current ?? result[0]?.id ?? null);
  }

  useEffect(() => {
    if (!authenticated) {
      setLoading(false);
      return;
    }
    void refreshTrips()
      .catch((cause: unknown) => {
        setError(cause instanceof ApiError ? cause.message : "Không thể tải chuyến đi.");
      })
      .finally(() => setLoading(false));
  }, [authenticated]);

  async function handleCreateTrip(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSubmitting(true);
    setError("");
    try {
      const trip = await createTrip(title.trim());
      setTitle("");
      setTrips((current) => [trip, ...current]);
      setSelectedTripId(trip.id);
    } catch (cause) {
      setError(cause instanceof ApiError ? cause.message : "Không thể tạo chuyến đi.");
    } finally {
      setSubmitting(false);
    }
  }

  if (!authenticated) {
    return (
      <main className="workspace-page">
        <Link to="/" className="wordmark">CITYPULSE®</Link>
        <section className="workspace-card">
          <p className="eyebrow">PLAN · INVITE · EXPLORE</p>
          <h1>Đăng nhập để xem chuyến đi</h1>
          <p className="workspace-muted">Bạn cần tài khoản để tạo chuyến đi và gửi lời mời cho bạn bè.</p>
          <Link className="btn btn-dark" to="/login">Đăng nhập</Link>
          <Link className="workspace-switch" to="/register">Tạo tài khoản mới</Link>
        </section>
      </main>
    );
  }

  const selectedTrip = trips.find((trip) => trip.id === selectedTripId);
  return (
    <main className="workspace-page">
      <header className="workspace-header">
        <Link to="/" className="wordmark">CITYPULSE®</Link>
        <Link to="/" className="workspace-back">Trang chủ</Link>
      </header>
      <section className="workspace-content">
        <p className="eyebrow">YOUR TRIPS · DA NANG</p>
        <h1>Chuyến đi của tôi</h1>
        <p className="workspace-muted">Tạo lịch trình, sau đó mời bạn bè cùng tham gia.</p>

        <form className="trip-create-form" onSubmit={handleCreateTrip}>
          <label htmlFor="trip-title">Tên chuyến đi</label>
          <div className="workspace-inline-form">
            <input
              id="trip-title"
              value={title}
              onChange={(event) => setTitle(event.target.value)}
              placeholder="Ví dụ: Cuối tuần ở Đà Nẵng"
              maxLength={200}
              required
            />
            <button className="btn btn-dark" type="submit" disabled={submitting}>
              {submitting ? "Đang tạo…" : "Tạo chuyến đi"}
            </button>
          </div>
        </form>

        {error && <p className="workspace-error" role="alert">{error}</p>}
        {loading ? (
          <p className="workspace-muted">Đang tải chuyến đi…</p>
        ) : trips.length === 0 ? (
          <p className="workspace-empty">Bạn chưa có chuyến đi nào. Hãy tạo chuyến đi đầu tiên ở trên.</p>
        ) : (
          <div className="trip-workspace">
            <section aria-label="Danh sách chuyến đi" className="trip-list">
              {trips.map((trip) => (
                <button
                  type="button"
                  key={trip.id}
                  className={`trip-list-item${trip.id === selectedTripId ? " is-selected" : ""}`}
                  onClick={() => setSelectedTripId(trip.id)}
                >
                  <span>{trip.title}</span>
                  <span aria-hidden="true">↗</span>
                </button>
              ))}
            </section>
            {selectedTrip && (
              <SendInvitationForm
                key={selectedTrip.id}
                tripId={selectedTrip.id}
                tripTitle={selectedTrip.title}
              />
            )}
          </div>
        )}
      </section>
    </main>
  );
}
