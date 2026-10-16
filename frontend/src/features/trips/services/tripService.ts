/**
 * Trip & Stop Service - API client contracts for DNVIBE / CITYPULSE
 */
import { apiGet, apiPost, apiPut, apiPatch, apiDelete } from "../../../lib/apiClient";

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

export const tripService = {
  listTrips(status?: TripStatus): Promise<TripResponse[]> {
    const query = status ? `?status=${status}` : "";
    return apiGet<TripResponse[]>(`/trips${query}`);
  },

  getTrip(tripId: string): Promise<TripDetailResponse> {
    return apiGet<TripDetailResponse>(`/trips/${tripId}`);
  },

  createTrip(req: CreateTripRequest): Promise<TripDetailResponse> {
    return apiPost<TripDetailResponse>("/trips", req);
  },

  updateTrip(tripId: string, req: UpdateTripRequest): Promise<TripDetailResponse> {
    return apiPut<TripDetailResponse>(`/trips/${tripId}`, req);
  },

  archiveTrip(tripId: string): Promise<TripResponse> {
    return apiPatch<TripResponse>(`/trips/${tripId}/archive`);
  },

  restoreTrip(tripId: string): Promise<TripResponse> {
    return apiPatch<TripResponse>(`/trips/${tripId}/restore`);
  },

  deleteTrip(tripId: string): Promise<void> {
    return apiDelete<void>(`/trips/${tripId}`);
  },

  addStop(tripId: string, req: CreateTripStopRequest): Promise<TripStopResponse> {
    return apiPost<TripStopResponse>(`/trips/${tripId}/stops`, req);
  },

  updateStop(tripId: string, stopId: string, req: UpdateTripStopRequest): Promise<TripStopResponse> {
    return apiPut<TripStopResponse>(`/trips/${tripId}/stops/${stopId}`, req);
  },

  deleteStop(tripId: string, stopId: string): Promise<void> {
    return apiDelete<void>(`/trips/${tripId}/stops/${stopId}`);
  },

  reorderStops(tripId: string, req: ReorderStopsRequest): Promise<TripDetailResponse> {
    return apiPut<TripDetailResponse>(`/trips/${tripId}/stops/reorder`, req);
  }
};

