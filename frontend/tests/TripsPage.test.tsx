import { beforeEach, describe, expect, it, vi } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import TripsPage from "../src/features/trips/components/TripsPage";
import * as tripService from "../src/features/trips/services/tripService";

vi.mock("../src/features/trips/components/SendInvitationForm.css", () => ({}));

describe("TripsPage", () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.setItem("citypulse.auth-token", "test-token");
  });

  it("requires sign-in before showing trips", () => {
    localStorage.clear();
    render(
      <MemoryRouter>
        <TripsPage />
      </MemoryRouter>
    );

    expect(screen.getByRole("link", { name: "Đăng nhập" })).toHaveAttribute("href", "/login");
  });

  it("lists trips, creates one, and displays its invitation form", async () => {
    const existingTrip = { id: 10, title: "Hành trình cũ", createdAt: "2026-01-01T00:00:00Z" };
    const createdTrip = { id: 11, title: "Đà Nẵng cuối tuần", createdAt: "2026-01-02T00:00:00Z" };
    vi.spyOn(tripService, "listTrips").mockResolvedValue([existingTrip]);
    vi.spyOn(tripService, "createTrip").mockResolvedValue(createdTrip);

    render(
      <MemoryRouter>
        <TripsPage />
      </MemoryRouter>
    );

    expect(await screen.findByRole("button", { name: /Hành trình cũ/ })).toBeInTheDocument();
    await userEvent.type(screen.getByLabelText("Tên chuyến đi"), "Đà Nẵng cuối tuần");
    await userEvent.click(screen.getByRole("button", { name: "Tạo chuyến đi" }));

    await waitFor(() => expect(screen.getByText("Đà Nẵng cuối tuần")).toBeInTheDocument());
    expect(screen.getByRole("heading", { name: "Mời thành viên" })).toBeInTheDocument();
    expect(tripService.createTrip).toHaveBeenCalledWith("Đà Nẵng cuối tuần");
  });
});
