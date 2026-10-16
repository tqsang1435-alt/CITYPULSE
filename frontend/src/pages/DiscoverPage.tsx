import React, { useState, useEffect, useCallback, useRef } from "react";
import DiscoverHeader from "../components/discover/DiscoverHeader";
import DiscoverFilterBar, { FilterState } from "../components/discover/DiscoverFilterBar";
import DiscoverPlaceList from "../components/discover/DiscoverPlaceList";
import MapPanel from "../components/map/MapPanel";
import { DiscoverPlaceItem } from "../data/mockPlaces";
import { placeService, mapSummaryToDiscoverItem, PlaceQueryParams } from "../services/placeService";
import "./DiscoverPage.css";

export const DiscoverPage: React.FC = () => {
  const [places, setPlaces] = useState<DiscoverPlaceItem[]>([]);
  const [selectedId, setSelectedId] = useState<string>("");
  const [mobileView, setMobileView] = useState<"list" | "map">("list");

  // Pagination & Sort state
  const [page, setPage] = useState<number>(0);
  const [size] = useState<number>(10);
  const [totalElements, setTotalElements] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(1);
  const [first, setFirst] = useState<boolean>(true);
  const [last, setLast] = useState<boolean>(true);
  const [sortBy, setSortBy] = useState<string>("rating");

  // Filter state
  const [filters, setFilters] = useState<FilterState>({
    search: "",
    district: "all",
    price: "all",
    status: "all",
    time: "all",
    distance: "all",
  });

  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Debounce search timer ref
  const searchTimeoutRef = useRef<number | null>(null);

  const fetchPlaces = useCallback(async () => {
    setIsLoading(true);
    setError(null);

    const queryParams: PlaceQueryParams = {
      page,
      size,
      sort: sortBy,
    };

    // Keyword search
    if (filters.search && filters.search.trim()) {
      queryParams.q = filters.search.trim();
    }

    // District filter
    if (filters.district !== "all") {
      const districtMap: Record<string, string> = {
        "son-tra": "Sơn Trà",
        "hai-chau": "Hải Châu",
        "hoa-vang": "Hòa Vang",
        "ngu-hanh-son": "Ngũ Hành Sơn",
        "lien-chieu": "Liên Chiểu",
        "thanh-khe": "Thanh Khê",
      };
      if (districtMap[filters.district]) {
        queryParams.district = districtMap[filters.district];
      }
    }

    // Price filter
    if (filters.price === "free") {
      queryParams.minPrice = 0;
      queryParams.maxPrice = 0;
    } else if (filters.price === "cheap") {
      queryParams.minPrice = 0;
      queryParams.maxPrice = 100000;
    } else if (filters.price === "medium") {
      queryParams.minPrice = 100000;
      queryParams.maxPrice = 500000;
    } else if (filters.price === "high") {
      queryParams.minPrice = 500000;
    }

    try {
      const res = await placeService.listPlaces(queryParams);
      const mapped = res.content.map((summary) => mapSummaryToDiscoverItem(summary));
      setPlaces(mapped);
      setTotalElements(res.totalElements);
      setTotalPages(res.totalPages);
      setFirst(res.first);
      setLast(res.last);

      // Auto-select first place if none selected or if previous selected place is missing
      if (mapped.length > 0) {
        if (!selectedId || !mapped.some((p) => p.id === selectedId)) {
          setSelectedId(mapped[0].id);
        }
      } else {
        setSelectedId("");
      }
    } catch {
      setError("Không thể tải danh sách địa điểm.");
    } finally {
      setIsLoading(false);
    }
  }, [page, size, sortBy, filters, selectedId]);

  useEffect(() => {
    if (searchTimeoutRef.current) {
      clearTimeout(searchTimeoutRef.current);
    }

    // Debounce 350ms
    searchTimeoutRef.current = window.setTimeout(() => {
      fetchPlaces();
    }, 350);

    return () => {
      if (searchTimeoutRef.current) {
        clearTimeout(searchTimeoutRef.current);
      }
    };
  }, [fetchPlaces]);

  const handleSelectPlace = (id: string) => {
    setSelectedId(id);
  };

  const handleFilterChange = (newFilters: FilterState) => {
    setFilters(newFilters);
    setPage(0); // Reset to first page whenever filter changes
  };

  const handleSortChange = (newSort: string) => {
    setSortBy(newSort);
    setPage(0);
  };

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
    window.scrollTo({ top: 220, behavior: "smooth" });
  };

  const handleToggleMobileView = () => {
    setMobileView((prev) => (prev === "list" ? "map" : "list"));
  };

  return (
    <div className="vista-discover-page">
      {/* 1. PAGE HEADER */}
      <DiscoverHeader />

      {/* 2. HORIZONTAL FILTER BAR */}
      <DiscoverFilterBar
        onFilterChange={handleFilterChange}
        onViewMapClick={() => {
          setMobileView("map");
          window.scrollTo({ top: 180, behavior: "smooth" });
        }}
      />

      {/* 3. MAIN SPLIT CONTENT AREA */}
      <main className="dnvibe-container vista-discover-main">
        {error ? (
          <div className="vista-discover-empty" style={{ width: "100%", margin: "40px auto" }}>
            <span className="material-symbols-outlined text-4xl" style={{ color: "#ef4444" }}>
              error
            </span>
            <p>{error}</p>
            <button
              type="button"
              onClick={fetchPlaces}
              className="vista-auth-card__submit-btn"
              style={{ width: "auto", padding: "8px 24px" }}
            >
              Thử lại
            </button>
          </div>
        ) : (
          <div className="vista-discover-split">
            {/* LEFT: PLACE LIST (46%) */}
            <div
              className={`vista-discover-split__left ${
                mobileView === "map" ? "vista-discover-split__left--hidden-mobile" : ""
              }`}
            >
              <DiscoverPlaceList
                places={places}
                selectedId={selectedId}
                onSelectPlace={handleSelectPlace}
                totalElements={totalElements}
                page={page}
                totalPages={totalPages}
                sortBy={sortBy}
                onSortChange={handleSortChange}
                onPageChange={handlePageChange}
                isLoading={isLoading}
                first={first}
                last={last}
              />
            </div>

            {/* RIGHT: MAP PANEL (54%) */}
            <div
              className={`vista-discover-split__right ${
                mobileView === "list" ? "vista-discover-split__right--hidden-mobile" : ""
              }`}
            >
              <MapPanel
                places={places}
                selectedId={selectedId}
                onSelectPlace={handleSelectPlace}
              />
            </div>
          </div>
        )}
      </main>

      {/* MOBILE FLOATING VIEW TOGGLE BUTTON */}
      <div className="vista-discover-mobile-toggle">
        <button
          type="button"
          className="vista-discover-mobile-toggle__btn"
          onClick={handleToggleMobileView}
          aria-label={mobileView === "list" ? "Chuyển sang xem bản đồ" : "Chuyển sang xem danh sách"}
        >
          <span className="material-symbols-outlined text-base">
            {mobileView === "list" ? "map" : "format_list_bulleted"}
          </span>
          <span>{mobileView === "list" ? "Xem bản đồ" : "Xem danh sách"}</span>
        </button>
      </div>
    </div>
  );
};

export default DiscoverPage;
