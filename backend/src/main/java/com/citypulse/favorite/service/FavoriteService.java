package com.citypulse.favorite.service;

import com.citypulse.common.dto.PageResponse;
import com.citypulse.common.exception.InvalidQueryParamException;
import com.citypulse.common.exception.PlaceNotFoundException;
import com.citypulse.favorite.dto.FavoriteItemResponse;
import com.citypulse.favorite.dto.FavoriteToggleResponse;
import com.citypulse.favorite.entity.Favorite;
import com.citypulse.favorite.repository.FavoriteRepository;
import com.citypulse.place.entity.Place;
import com.citypulse.place.entity.PlaceStatus;
import com.citypulse.place.repository.PlaceRepository;
import com.citypulse.user.entity.User;
import com.citypulse.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class FavoriteService {

    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );

    private final FavoriteRepository favoriteRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;

    public FavoriteService(
            FavoriteRepository favoriteRepository,
            PlaceRepository placeRepository,
            UserRepository userRepository
    ) {
        this.favoriteRepository = favoriteRepository;
        this.placeRepository = placeRepository;
        this.userRepository = userRepository;
    }

    /**
     * Adds a published place to current user's favorites.
     * Idempotent: returns existing favorite status if already present.
     */
    @Transactional
    public FavoriteToggleResponse addFavorite(UUID userId, String placeIdOrSlug) {
        Place place = resolvePublishedPlace(placeIdOrSlug);

        Optional<Favorite> existing = favoriteRepository.findByUserIdAndPlaceId(userId, place.getId());
        if (existing.isPresent()) {
            return new FavoriteToggleResponse(place.getId(), true, "Địa điểm đã có trong danh sách yêu thích.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy thông tin tài khoản người dùng."));

        Favorite favorite = new Favorite(user, place);
        favoriteRepository.save(favorite);

        return new FavoriteToggleResponse(place.getId(), true, "Đã thêm vào danh sách yêu thích thành công.");
    }

    /**
     * Removes a place from current user's favorites.
     * Idempotent: ensures only current user's favorite record is deleted.
     */
    @Transactional
    public FavoriteToggleResponse removeFavorite(UUID userId, String placeIdOrSlug) {
        Place place = resolveAnyPlace(placeIdOrSlug);

        Optional<Favorite> existing = favoriteRepository.findByUserIdAndPlaceId(userId, place.getId());
        if (existing.isEmpty()) {
            return new FavoriteToggleResponse(place.getId(), false, "Địa điểm không có trong danh sách yêu thích.");
        }

        favoriteRepository.deleteByUserIdAndPlaceId(userId, place.getId());
        return new FavoriteToggleResponse(place.getId(), false, "Đã xóa khỏi danh sách yêu thích thành công.");
    }

    /**
     * Lists all favorites of the current user with pagination.
     * Only returns places that are currently PUBLISHED.
     */
    @Transactional(readOnly = true)
    public PageResponse<FavoriteItemResponse> listFavorites(UUID userId, int page, int size) {
        if (page < 0) {
            throw new InvalidQueryParamException("Tham số 'page' không được âm.");
        }
        if (size < 1 || size > 100) {
            throw new InvalidQueryParamException("Tham số 'size' phải nằm trong khoảng từ 1 đến 100.");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Favorite> pageResult = favoriteRepository.findByUserIdAndPlaceStatus(
            userId, PlaceStatus.PUBLISHED, pageable
        );

        Page<FavoriteItemResponse> dtoPage = pageResult.map(FavoriteItemResponse::fromEntity);
        return PageResponse.fromPage(dtoPage);
    }

    /**
     * Checks if a place is favorited by a user.
     */
    @Transactional(readOnly = true)
    public boolean isPlaceFavorited(UUID userId, UUID placeId) {
        return favoriteRepository.existsByUserIdAndPlaceId(userId, placeId);
    }

    private Place resolvePublishedPlace(String idOrSlug) {
        if (idOrSlug == null || idOrSlug.trim().isBlank()) {
            throw new PlaceNotFoundException("Địa điểm không tồn tại hoặc chưa được công khai.");
        }

        String trimmed = idOrSlug.trim();
        if (UUID_PATTERN.matcher(trimmed).matches()) {
            UUID id = UUID.fromString(trimmed);
            return placeRepository.findByIdAndStatus(id, PlaceStatus.PUBLISHED)
                    .orElseThrow(() -> new PlaceNotFoundException("Địa điểm không tồn tại hoặc chưa được công khai."));
        } else {
            String slug = trimmed.toLowerCase(Locale.ROOT);
            return placeRepository.findBySlugAndStatus(slug, PlaceStatus.PUBLISHED)
                    .orElseThrow(() -> new PlaceNotFoundException("Địa điểm không tồn tại hoặc chưa được công khai."));
        }
    }

    private Place resolveAnyPlace(String idOrSlug) {
        if (idOrSlug == null || idOrSlug.trim().isBlank()) {
            throw new PlaceNotFoundException("Địa điểm không tồn tại.");
        }

        String trimmed = idOrSlug.trim();
        if (UUID_PATTERN.matcher(trimmed).matches()) {
            UUID id = UUID.fromString(trimmed);
            return placeRepository.findById(id)
                    .orElseThrow(() -> new PlaceNotFoundException("Địa điểm không tồn tại."));
        } else {
            String slug = trimmed.toLowerCase(Locale.ROOT);
            return placeRepository.findBySlug(slug)
                    .orElseThrow(() -> new PlaceNotFoundException("Địa điểm không tồn tại."));
        }
    }
}
