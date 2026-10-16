// Frontend test cho SendInvitationForm (UC-01)
// Chạy với: npm test (vitest)
//
// Lưu ý: Tests này mock tripService — không cần backend chạy.

import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import SendInvitationForm from "../src/features/trips/components/SendInvitationForm";
import * as tripService from "../src/features/trips/services/tripService";
import { ApiError } from "../src/lib/apiClient";

// Mock CSS imports (vitest không xử lý CSS)
vi.mock("../src/features/trips/components/SendInvitationForm.css", () => ({}));

const mockInvitation: tripService.InvitationResponse = {
  id: 1,
  tripId: 10,
  tripTitle: "Hành trình Đà Nẵng",
  inviteeEmail: "member@example.com",
  status: "pending",
  expiresAt: new Date(Date.now() + 7 * 24 * 3600 * 1000).toISOString(),
  createdAt: new Date().toISOString(),
};

const DEFAULT_PROPS = {
  tripId: 10,
  tripTitle: "Hành trình Đà Nẵng",
};

describe("SendInvitationForm — UC-01", () => {
  beforeEach(() => {
    vi.restoreAllMocks();
  });

  // ── 1. Render cơ bản ─────────────────────────────────────────────────────

  it("hiển thị form nhập email và nút gửi lời mời", () => {
    render(<SendInvitationForm {...DEFAULT_PROPS} />);
    expect(screen.getByLabelText(/địa chỉ email/i)).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /gửi lời mời/i })).toBeInTheDocument();
  });

  // ── 2. Thành công ─────────────────────────────────────────────────────────

  it("hiển thị thông báo thành công sau khi gửi lời mời", async () => {
    vi.spyOn(tripService, "sendTripInvitation").mockResolvedValue(mockInvitation);

    render(<SendInvitationForm {...DEFAULT_PROPS} />);
    await userEvent.type(screen.getByLabelText(/địa chỉ email/i), "member@example.com");
    await userEvent.click(screen.getByRole("button", { name: /gửi lời mời/i }));

    await waitFor(() => {
      expect(screen.getByText(/lời mời đã được gửi/i)).toBeInTheDocument();
      expect(screen.getByText(/member@example\.com/i)).toBeInTheDocument();
    });
  });

  // ── 3. Trạng thái loading ─────────────────────────────────────────────────

  it("hiển thị 'Đang gửi…' và disable nút khi đang gửi", async () => {
    vi.spyOn(tripService, "sendTripInvitation").mockImplementation(
      () => new Promise(() => {}) // không bao giờ resolve
    );

    render(<SendInvitationForm {...DEFAULT_PROPS} />);
    await userEvent.type(screen.getByLabelText(/địa chỉ email/i), "member@example.com");
    await userEvent.click(screen.getByRole("button", { name: /gửi lời mời/i }));

    expect(screen.getByText(/đang gửi/i)).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /đang gửi/i })).toBeDisabled();
  });

  // ── 4. Duplicate pending invitation → hiển thị lỗi 409 ──────────────────

  it("hiển thị lỗi nghiệp vụ khi invitation đã tồn tại", async () => {
    vi.spyOn(tripService, "sendTripInvitation").mockRejectedValue(
      new ApiError(409, "Lời mời đã được gửi trước đó đến member@example.com")
    );

    render(<SendInvitationForm {...DEFAULT_PROPS} />);
    await userEvent.type(screen.getByLabelText(/địa chỉ email/i), "member@example.com");
    await userEvent.click(screen.getByRole("button", { name: /gửi lời mời/i }));

    await waitFor(() => {
      expect(screen.getByRole("alert")).toHaveTextContent(/lời mời đã được gửi trước đó/i);
    });
  });

  // ── 5. Không phải owner → hiển thị lỗi 403 ───────────────────────────────

  it("hiển thị lỗi khi không phải owner", async () => {
    vi.spyOn(tripService, "sendTripInvitation").mockRejectedValue(
      new ApiError(403, "Chỉ owner của chuyến đi mới có thể gửi lời mời.")
    );

    render(<SendInvitationForm {...DEFAULT_PROPS} />);
    await userEvent.type(screen.getByLabelText(/địa chỉ email/i), "member@example.com");
    await userEvent.click(screen.getByRole("button", { name: /gửi lời mời/i }));

    await waitFor(() => {
      expect(screen.getByRole("alert")).toHaveTextContent(/chỉ owner/i);
    });
  });

  // ── 6. Email trống → nút disabled ────────────────────────────────────────

  it("nút gửi bị disable khi email trống", () => {
    render(<SendInvitationForm {...DEFAULT_PROPS} />);
    expect(screen.getByRole("button", { name: /gửi lời mời/i })).toBeDisabled();
  });

  // ── 7. Sau khi thành công, gõ lại để gửi thêm ────────────────────────────

  it("có thể gửi thêm lời mời sau khi thành công", async () => {
    vi.spyOn(tripService, "sendTripInvitation").mockResolvedValue(mockInvitation);

    render(<SendInvitationForm {...DEFAULT_PROPS} />);
    await userEvent.type(screen.getByLabelText(/địa chỉ email/i), "member@example.com");
    await userEvent.click(screen.getByRole("button", { name: /gửi lời mời/i }));

    await waitFor(() => expect(screen.getByText(/lời mời đã được gửi/i)).toBeInTheDocument());

    // Click "Gửi thêm"
    await userEvent.click(screen.getByRole("button", { name: /gửi thêm/i }));
    expect(screen.getByLabelText(/địa chỉ email/i)).toBeInTheDocument();
  });
});
