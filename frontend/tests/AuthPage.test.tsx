import { beforeEach, describe, expect, it, vi } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import AuthPage from "../src/features/auth/components/AuthPage";
import * as authService from "../src/features/auth/services/authService";
import { ApiError } from "../src/lib/apiClient";

describe("AuthPage", () => {
  beforeEach(() => {
    vi.restoreAllMocks();
  });

  it("logs in and navigates to trips", async () => {
    vi.spyOn(authService, "login").mockResolvedValue({
      id: 1,
      email: "owner@example.com",
      displayName: "Owner",
    });
    render(
      <MemoryRouter initialEntries={["/login"]}>
        <Routes>
          <Route path="/login" element={<AuthPage mode="login" />} />
          <Route path="/trips" element={<p>Đích chuyến đi</p>} />
        </Routes>
      </MemoryRouter>
    );

    await userEvent.type(screen.getByLabelText("Email"), "owner@example.com");
    await userEvent.type(screen.getByLabelText("Mật khẩu"), "correct-horse");
    await userEvent.click(screen.getByRole("button", { name: "Đăng nhập" }));

    await waitFor(() => expect(screen.getByText("Đích chuyến đi")).toBeInTheDocument());
    expect(authService.login).toHaveBeenCalledWith("owner@example.com", "correct-horse");
  });

  it("shows duplicate-account errors during registration", async () => {
    vi.spyOn(authService, "register").mockRejectedValue(new ApiError(409, "Email đã đăng ký"));
    render(
      <MemoryRouter>
        <AuthPage mode="register" />
      </MemoryRouter>
    );

    await userEvent.type(screen.getByLabelText("Tên hiển thị"), "Owner");
    await userEvent.type(screen.getByLabelText("Email"), "owner@example.com");
    await userEvent.type(screen.getByLabelText("Mật khẩu"), "correct-horse");
    await userEvent.click(screen.getByRole("button", { name: "Đăng ký" }));

    await waitFor(() => expect(screen.getByRole("alert")).toHaveTextContent("Email đã đăng ký"));
  });
});
