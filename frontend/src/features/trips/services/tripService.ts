// Trip & Invitation API service
import { apiGet, apiPost } from "../../../lib/apiClient";

export interface InvitationResponse {
  id: number;
  tripId: number;
  tripTitle: string;
  inviteeEmail: string;
  status: "pending" | "accepted" | "declined" | "expired";
  expiresAt: string;
  createdAt: string;
}

export interface TripSummary {
  id: number;
  title: string;
  createdAt: string;
}

/**
 * UC-01: Gửi lời mời tham gia chuyến đi.
 *
 * @param tripId      ID chuyến đi
 * @param email       email người được mời
 */
export async function sendTripInvitation(
  tripId: number,
  email: string
): Promise<InvitationResponse> {
  return apiPost<InvitationResponse>(
    `/trips/${tripId}/invitations`,
    { email }
  );
}

export async function listTrips(): Promise<TripSummary[]> {
  const result = await apiGet<{ items: TripSummary[] }>("/trips");
  return result.items;
}

export async function createTrip(title: string): Promise<TripSummary> {
  return apiPost<TripSummary>("/trips", { title });
}
