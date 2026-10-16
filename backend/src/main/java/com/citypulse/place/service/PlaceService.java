package com.citypulse.place.service;

import com.citypulse.common.dto.PageResponse;
import com.citypulse.common.exception.InvalidQueryParamException;
import com.citypulse.common.exception.PlaceNotFoundException;
import com.citypulse.place.dto.PlaceDetailResponse;
import com.citypulse.place.dto.PlaceSummaryResponse;
import com.citypulse.place.entity.Place;
import com.citypulse.place.entity.PlaceStatus;
import com.citypulse.place.repository.PlaceRepository;
import com.citypulse.place.repository.PlaceSpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class PlaceService {

    private static final Logger log = LoggerFactory.getLogger(PlaceService.class);

    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );

    private final PlaceRepository placeRepository;

    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    /**
     * Lists published places with pagination, filtering, searching and sorting.
     */
    @Transactional(readOnly = true)
    public PageResponse<PlaceSummaryResponse> listPublishedPlaces(
            int page,
            int size,
            String q,
            String category,
            String district,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Double minRating,
            String sort,
            Boolean openNow
    ) {
        // 1. Validation
        if (page < 0) {
            throw new InvalidQueryParamException("Tham số 'page' không được âm.");
        }
        if (size < 1 || size > 100) {
            throw new InvalidQueryParamException("Tham số 'size' phải nằm trong khoảng từ 1 đến 100.");
        }
        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidQueryParamException("Tham số 'minPrice' không được âm.");
        }
        if (maxPrice != null && minPrice != null && maxPrice.compareTo(minPrice) < 0) {
            throw new InvalidQueryParamException("Tham số 'maxPrice' phải lớn hơn hoặc bằng 'minPrice'.");
        }
        if (minRating != null && (minRating < 0.0 || minRating > 5.0)) {
            throw new InvalidQueryParamException("Tham số 'minRating' phải nằm trong khoảng từ 0.0 đến 5.0.");
        }

        if (openNow != null && openNow) {
            log.info("Lọc theo openNow hiện chưa triển khai vì cấu trúc OpeningHours chưa đủ trong schema.");
        }

        // 2. Resolve sorting
        Sort springSort = resolveSort(sort);

        Pageable pageable = PageRequest.of(page, size, springSort);

        // 3. Build Specification
        Specification<Place> spec = PlaceSpecifications.withFilters(
            q,
            category,
            district,
            minPrice,
            maxPrice,
            minRating,
            PlaceStatus.PUBLISHED
        );

        // 4. Query repository
        Page<Place> placePage = placeRepository.findAll(spec, pageable);

        Page<PlaceSummaryResponse> dtoPage = placePage.map(PlaceSummaryResponse::fromEntity);
        return PageResponse.fromPage(dtoPage);
    }

    /**
     * Retrieves detail of a published place by its UUID or unique slug.
     */
    @Transactional(readOnly = true)
    public PlaceDetailResponse getPlaceByIdOrSlug(String idOrSlug) {
        if (idOrSlug == null || idOrSlug.trim().isBlank()) {
            throw new PlaceNotFoundException("Địa điểm không tồn tại hoặc chưa được công khai.");
        }

        String trimmed = idOrSlug.trim();
        Place place;

        if (UUID_PATTERN.matcher(trimmed).matches()) {
            UUID id = UUID.fromString(trimmed);
            place = placeRepository.findByIdAndStatus(id, PlaceStatus.PUBLISHED)
                    .orElseThrow(() -> new PlaceNotFoundException("Địa điểm không tồn tại hoặc chưa được công khai."));
        } else {
            String slug = trimmed.toLowerCase(Locale.ROOT);
            place = placeRepository.findBySlugAndStatus(slug, PlaceStatus.PUBLISHED)
                    .orElseThrow(() -> new PlaceNotFoundException("Địa điểm không tồn tại hoặc chưa được công khai."));
        }

        return PlaceDetailResponse.fromEntity(place);
    }

    /**
     * Resolves and validates sort parameter against safe whitelist.
     */
    private Sort resolveSort(String sort) {
        if (sort == null || sort.trim().isBlank()) {
            return Sort.by(Sort.Direction.DESC, "ratingAverage");
        }

        String normalized = sort.trim().toLowerCase(Locale.ROOT);

        return switch (normalized) {
            case "rating", "rating,desc", "ratingaverage,desc" -> Sort.by(Sort.Direction.DESC, "ratingAverage");
            case "rating,asc", "ratingaverage,asc" -> Sort.by(Sort.Direction.ASC, "ratingAverage");
            case "name", "name,asc" -> Sort.by(Sort.Direction.ASC, "name");
            case "name,desc" -> Sort.by(Sort.Direction.DESC, "name");
            case "newest", "createdat,desc", "created_at,desc" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "oldest", "createdat,asc", "created_at,asc" -> Sort.by(Sort.Direction.ASC, "createdAt");
            case "price", "price,asc", "pricemin,asc", "price_min,asc" -> Sort.by(Sort.Direction.ASC, "priceMin");
            case "price,desc", "pricemin,desc", "price_min,desc" -> Sort.by(Sort.Direction.DESC, "priceMin");
            default -> throw new InvalidQueryParamException(
                "Tham số 'sort' không hợp lệ: '" + sort + "'. Các giá trị hỗ trợ: rating, rating,asc, name, name,desc, newest, price, price,desc."
            );
        };
    }
}
