/**
 * Trip & Stop Service - API client contracts for DNVIBE / CITYPULSE
 */

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api/v1";

export type TripStatus = "DRAFT" | "ACTIVE" | "COMPLETED" | "ARCHIVED";

export interface TripResponse {
  id: string;
  ownerId: string;
  title: string;
  description?: string | null;
  startDate: string;
  endDate: string;
  budget?: number | null;
  status: TripStatus;
  totalDays: number;
  stopCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface TripStopResponse {
  id: string;
  tripId: string;
  placeId?: string | null;
  customName?: string | null;
  latitude?: number | null;
  longitude?: number | null;
  dayNumber: number;
  orderIndex: number;
  arrivalTime?: string | null;
  departureTime?: string | null;
  estimatedCost?: number | null;
  note?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface TripDetailResponse extends TripResponse {
  totalEstimatedCost: number;
  stops: TripStopResponse[];
}

export interface CreateTripRequest {
  title: string;
  description?: string;
  startDate: string;
  endDate: string;
  budget?: number;
}

export interface UpdateTripRequest {
  title: string;
  description?: string;
  startDate: string;
  endDate: string;
  budget?: number;
}

export interface CreateTripStopRequest {
  placeId?: string;
  customName?: string;
  latitude?: number;
  longitude?: number;
  dayNumber?: number;
  orderIndex?: number;
  arrivalTime?: string;
  departureTime?: string;
  estimatedCost?: number;
  note?: string;
}

export interface UpdateTripStopRequest extends CreateTripStopRequest {}

export interface ReorderStopsRequest {
  items: Array<{
    stopId: string;
    dayNumber: number;
    orderIndex: number;
  }>;
}

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
      ...(options?.headers || {})
    },
    ...options
  });

  if (!response.ok) {
    let errorDetail = `Status ${response.status}`;
    try {
      const errJson = await response.json();
      if (errJson?.message) errorDetail = errJson.message;
    } catch {
      // ignore
    }
    throw new Error(`API error: ${errorDetail}`);
  }

  if (response.status === 204) {
    return undefined as unknown as T;
  }

  return response.json() as Promise<T>;
}

export const tripService = {
  listTrips(status?: TripStatus): Promise<TripResponse[]> {
    const query = status ? `?status=${status}` : "";
    return request<TripResponse[]>(`/trips${query}`);
  },

  getTrip(tripId: string): Promise<TripDetailResponse> {
    return request<TripDetailResponse>(`/trips/${tripId}`);
  },

  createTrip(req: CreateTripRequest): Promise<TripDetailResponse> {
    return request<TripDetailResponse>("/trips", {
      method: "POST",
      body: JSON.stringify(req)
    });
  },

  updateTrip(tripId: string, req: UpdateTripRequest): Promise<TripDetailResponse> {
    return request<TripDetailResponse>(`/trips/${tripId}`, {
      method: "PUT",
      body: JSON.stringify(req)
    });
  },

  archiveTrip(tripId: string): Promise<TripResponse> {
    return request<TripResponse>(`/trips/${tripId}/archive`, {
      method: "PATCH"
    });
  },

  restoreTrip(tripId: string): Promise<TripResponse> {
    return request<TripResponse>(`/trips/${tripId}/restore`, {
      method: "PATCH"
    });
  },

  deleteTrip(tripId: string): Promise<void> {
    return request<void>(`/trips/${tripId}`, {
      method: "DELETE"
    });
  },

  addStop(tripId: string, req: CreateTripStopRequest): Promise<TripStopResponse> {
    return request<TripStopResponse>(`/trips/${tripId}/stops`, {
      method: "POST",
      body: JSON.stringify(req)
    });
  },

  updateStop(tripId: string, stopId: string, req: UpdateTripStopRequest): Promise<TripStopResponse> {
    return request<TripStopResponse>(`/trips/${tripId}/stops/${stopId}`, {
      method: "PUT",
      body: JSON.stringify(req)
    });
  },

  deleteStop(tripId: string, stopId: string): Promise<void> {
    return request<void>(`/trips/${tripId}/stops/${stopId}`, {
      method: "DELETE"
    });
  },

  reorderStops(tripId: string, req: ReorderStopsRequest): Promise<TripDetailResponse> {
    return request<TripDetailResponse>(`/trips/${tripId}/stops/reorder`, {
      method: "PUT",
      body: JSON.stringify(req)
    });
  }
};
