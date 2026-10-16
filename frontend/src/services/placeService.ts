import { apiGet } from "../lib/apiClient";
import { PageResponse, PlaceDetailResponse, PlaceSummaryResponse } from "../types/api";
import { DiscoverPlaceItem, PlaceItem } from "../data/mockPlaces";

export interface PlaceQueryParams {
  page?: number;
  size?: number;
  q?: string;
  category?: string;
  district?: string;
  minPrice?: number;
  maxPrice?: number;
  minRating?: number;
  sort?: string;
  openNow?: boolean;
}

const KNOWN_MAP_COORDS: Record<string, { x: number; y: number }> = {
  "cau-rong": { x: 44, y: 45 },
  "chua-linh-ung": { x: 75, y: 15 },
  "ban-dao-son-tra": { x: 77, y: 12 },
  "bai-bien-my-khe": { x: 70, y: 47 },
  "my-khe-beach": { x: 70, y: 47 },
  "ba-na-hills": { x: 15, y: 28 },
  "bao-tang-cham": { x: 42, y: 47 },
  "cho-con": { x: 37, y: 33 },
  "cau-song-han": { x: 42, y: 28 },
  "ngu-hanh-son": { x: 74, y: 77 },
  "bien-non-nuoc": { x: 76, y: 73 },
  "ran-nam-o": { x: 22, y: 18 },
  "cho-han": { x: 41, y: 33 },
  "cong-vien-apec": { x: 43, y: 49 },
  "cau-tinh-yeu": { x: 46, y: 44 },
  "bai-rang-son-tra": { x: 78, y: 13 },
};

export function geoCoordsToMapPercent(
  slug: string,
  latitude: number | null,
  longitude: number | null
): { x: number; y: number } {
  if (KNOWN_MAP_COORDS[slug]) {
    return KNOWN_MAP_COORDS[slug];
  }

  if (latitude != null && longitude != null) {
    // Da Nang bounding box projection for map SVG:
    // Longitude: 108.00 (west) to 108.32 (east)
    // Latitude: 15.98 (south) to 16.15 (north)
    const minLon = 108.0;
    const maxLon = 108.32;
    const minLat = 15.98;
    const maxLat = 16.15;

    const x = Math.min(Math.max(((longitude - minLon) / (maxLon - minLon)) * 100, 10), 90);
    // Invert latitude because SVG Y goes downward
    const y = Math.min(Math.max(((maxLat - latitude) / (maxLat - minLat)) * 100, 10), 90);

    return { x: Math.round(x), y: Math.round(y) };
  }

  return { x: 50, y: 50 };
}

export function formatPrice(min: number, max: number): string {
  if (min === 0 && max === 0) {
    return "Miễn phí";
  }
  if (min === 0 && max > 0) {
    return `Đến ${max.toLocaleString("vi-VN")}đ`;
  }
  if (min > 0 && max > 0 && min === max) {
    return `${min.toLocaleString("vi-VN")}đ`;
  }
  if (min > 0 && max > 0 && min !== max) {
    return `${min.toLocaleString("vi-VN")} – ${max.toLocaleString("vi-VN")}đ`;
  }
  return "Miễn phí";
}

export function parseTags(tags: string | null): string[] {
  if (!tags) return [];
  return tags
    .split(/[,;]/)
    .map((t) => t.trim())
    .filter(Boolean);
}

export function mapCategoryToUI(cat: string): {
  type: "beach" | "sightseeing" | "culture" | "food";
  iconName: string;
} {
  const lower = (cat || "").toLowerCase();
  if (lower.includes("biển") || lower.includes("thể thao nước")) {
    return { type: "beach", iconName: "beach_access" };
  }
  if (lower.includes("ẩm thực") || lower.includes("đặc sản") || lower.includes("chợ")) {
    return { type: "food", iconName: "restaurant" };
  }
  if (lower.includes("văn hóa") || lower.includes("lịch sử") || lower.includes("bảo tàng") || lower.includes("cầu")) {
    return { type: "culture", iconName: "museum" };
  }
  return { type: "sightseeing", iconName: "landscape" };
}

export function mapSummaryToDiscoverItem(
  summary: PlaceSummaryResponse,
  isFavorite = false
): DiscoverPlaceItem {
  const cat = mapCategoryToUI(summary.category);
  const tags = parseTags(summary.tags);
  if (tags.length === 0) {
    tags.push(summary.category);
  }

  return {
    id: summary.slug || summary.id,
    name: summary.name,
    district: summary.district,
    rating: Number(summary.ratingAverage) || 4.5,
    reviewCount: `${summary.reviewCount || 0}`,
    tags,
    price: formatPrice(Number(summary.priceMin) || 0, Number(summary.priceMax) || 0),
    bestTime: summary.bestTime || "Quanh năm",
    crowdLevel: "Lý tưởng",
    crowdLevelType: "ideal",
    imageUrl: summary.thumbnailUrl || "/images/my-khe.png",
    isFavorite,
    isFeatured: (summary.ratingAverage || 0) >= 4.8,
    mapCoords: geoCoordsToMapPercent(summary.slug, summary.latitude, summary.longitude),
    shortSummary: summary.address,
    category: cat.type,
    iconName: cat.iconName,
  };
}

export function mapSummaryToPlaceItem(
  summary: PlaceSummaryResponse,
  isFavorite = false
): PlaceItem {
  return {
    id: summary.slug || summary.id,
    name: summary.name,
    district: summary.district,
    categoryTag: summary.category,
    rating: Number(summary.ratingAverage) || 4.5,
    reviewCount: `${summary.reviewCount || 0}`,
    price: formatPrice(Number(summary.priceMin) || 0, Number(summary.priceMax) || 0),
    bestTime: summary.bestTime || "Quanh năm",
    crowdLevel: "Lý tưởng",
    crowdLevelType: "ideal",
    imageUrl: summary.thumbnailUrl || "/images/my-khe.png",
    description: summary.address || summary.name,
    isFavorite,
  };
}

export const placeService = {
  /**
   * Retrieves published places with filters, search, sort, and pagination.
   */
  async listPlaces(params: PlaceQueryParams = {}): Promise<PageResponse<PlaceSummaryResponse>> {
    const query = new URLSearchParams();

    if (params.page !== undefined) query.set("page", String(params.page));
    if (params.size !== undefined) query.set("size", String(params.size));
    if (params.q && params.q.trim()) query.set("q", params.q.trim());
    if (params.category && params.category !== "all") query.set("category", params.category);
    if (params.district && params.district !== "all") query.set("district", params.district);
    if (params.minPrice !== undefined) query.set("minPrice", String(params.minPrice));
    if (params.maxPrice !== undefined) query.set("maxPrice", String(params.maxPrice));
    if (params.minRating !== undefined) query.set("minRating", String(params.minRating));
    if (params.sort) query.set("sort", params.sort);
    if (params.openNow !== undefined) query.set("openNow", String(params.openNow));

    const qs = query.toString();
    const path = `/places${qs ? `?${qs}` : ""}`;
    return apiGet<PageResponse<PlaceSummaryResponse>>(path);
  },

  /**
   * Retrieves place detail by UUID or slug.
   */
  async getPlaceByIdOrSlug(idOrSlug: string): Promise<PlaceDetailResponse> {
    return apiGet<PlaceDetailResponse>(`/places/${encodeURIComponent(idOrSlug)}`);
  },
};
