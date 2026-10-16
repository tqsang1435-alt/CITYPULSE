/**
 * TypeScript definitions for CityPulse / VISTA API responses and requests.
 * Aligned with Spring Boot backend DTOs (Auth, Place, Favorite, Page).
 */

export type Role = "USER" | "ADMIN";

export type UserStatus = "ACTIVE" | "LOCKED" | "PENDING";

export interface User {
  id: string;
  email: string;
  displayName: string;
  avatarUrl: string | null;
  role: Role;
  status: UserStatus;
  createdAt: string;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresInMs: number;
  user: User;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  displayName: string;
  email: string;
  password: string;
}

export type PlaceStatus = "DRAFT" | "PUBLISHED" | "ARCHIVED";

export interface PlaceSummaryResponse {
  id: string;
  slug: string;
  name: string;
  category: string;
  district: string;
  address: string;
  latitude: number | null;
  longitude: number | null;
  thumbnailUrl: string;
  priceMin: number;
  priceMax: number;
  visitDurationMin: number;
  ratingAverage: number;
  reviewCount: number;
  tags: string | null;
  bestTime: string | null;
}

export interface PlaceDetailResponse {
  id: string;
  slug: string;
  name: string;
  category: string;
  district: string;
  address: string;
  latitude: number | null;
  longitude: number | null;
  status: PlaceStatus;
  shortDescription: string;
  longDescription: string;
  thumbnailUrl: string;
  coverImageUrl: string | null;
  priceMin: number;
  priceMax: number;
  visitDurationMin: number;
  ratingAverage: number;
  reviewCount: number;
  tags: string | null;
  bestTime: string | null;
  dataQuality: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface FavoriteToggleResponse {
  placeId: string;
  isFavorite: boolean;
  message: string;
}

export interface FavoriteItemResponse {
  favoriteId: string;
  favoritedAt: string;
  place: PlaceSummaryResponse;
}

export interface ApiErrorResponse {
  status: number;
  error: string;
  message: string;
  path?: string;
  errors?: Record<string, string>;
}
