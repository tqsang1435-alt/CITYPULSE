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
import com.citypulse.user.entity.Role;
import com.citypulse.user.entity.User;
import com.citypulse.user.entity.UserStatus;
import com.citypulse.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    private UUID userId;
    private UUID placeId;
    private User sampleUser;
    private Place samplePlace;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        placeId = UUID.randomUUID();

        sampleUser = new User("user@danangvista.vn", "hash", "Demo User", Role.USER, UserStatus.ACTIVE);
        sampleUser.setId(userId);

        samplePlace = new Place("bai-bien-my-khe", "Bãi biển Mỹ Khê", "BEACH", "Quận Sơn Trà");
        samplePlace.setId(placeId);
        samplePlace.setStatus(PlaceStatus.PUBLISHED);
    }

    @Test
    @DisplayName("Should add place to favorites successfully")
    void shouldAddFavoriteSuccessfully() {
        when(placeRepository.findByIdAndStatus(placeId, PlaceStatus.PUBLISHED))
                .thenReturn(Optional.of(samplePlace));
        when(favoriteRepository.findByUserIdAndPlaceId(userId, placeId))
                .thenReturn(Optional.empty());
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(sampleUser));

        FavoriteToggleResponse response = favoriteService.addFavorite(userId, placeId.toString());

        assertThat(response.isFavorite()).isTrue();
        assertThat(response.placeId()).isEqualTo(placeId);
        assertThat(response.message()).contains("thành công");
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    @DisplayName("Should return idempotent success if place is already favorited")
    void shouldBeIdempotentWhenAlreadyFavorited() {
        when(placeRepository.findByIdAndStatus(placeId, PlaceStatus.PUBLISHED))
                .thenReturn(Optional.of(samplePlace));
        Favorite existing = new Favorite(sampleUser, samplePlace);
        when(favoriteRepository.findByUserIdAndPlaceId(userId, placeId))
                .thenReturn(Optional.of(existing));

        FavoriteToggleResponse response = favoriteService.addFavorite(userId, placeId.toString());

        assertThat(response.isFavorite()).isTrue();
        assertThat(response.placeId()).isEqualTo(placeId);
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    @DisplayName("Should throw PlaceNotFoundException when adding favorite for unknown place")
    void shouldThrowWhenAddingUnknownPlace() {
        when(placeRepository.findByIdAndStatus(placeId, PlaceStatus.PUBLISHED))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> favoriteService.addFavorite(userId, placeId.toString()))
                .isInstanceOf(PlaceNotFoundException.class)
                .hasMessageContaining("Địa điểm không tồn tại hoặc chưa được công khai");

        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    @DisplayName("Should remove place from favorites successfully")
    void shouldRemoveFavoriteSuccessfully() {
        when(placeRepository.findById(placeId))
                .thenReturn(Optional.of(samplePlace));
        Favorite existing = new Favorite(sampleUser, samplePlace);
        when(favoriteRepository.findByUserIdAndPlaceId(userId, placeId))
                .thenReturn(Optional.of(existing));

        FavoriteToggleResponse response = favoriteService.removeFavorite(userId, placeId.toString());

        assertThat(response.isFavorite()).isFalse();
        assertThat(response.placeId()).isEqualTo(placeId);
        assertThat(response.message()).contains("Đã xóa khỏi danh sách yêu thích");
        verify(favoriteRepository).deleteByUserIdAndPlaceId(userId, placeId);
    }

    @Test
    @DisplayName("Should safely return false when removing non-favorited place")
    void shouldHandleRemovingNonFavoritedPlace() {
        when(placeRepository.findById(placeId))
                .thenReturn(Optional.of(samplePlace));
        when(favoriteRepository.findByUserIdAndPlaceId(userId, placeId))
                .thenReturn(Optional.empty());

        FavoriteToggleResponse response = favoriteService.removeFavorite(userId, placeId.toString());

        assertThat(response.isFavorite()).isFalse();
        assertThat(response.message()).contains("không có trong danh sách yêu thích");
        verify(favoriteRepository, never()).deleteByUserIdAndPlaceId(any(), any());
    }

    @Test
    @DisplayName("Should list current user's favorites with pagination")
    void shouldListFavoritesForCurrentUser() {
        Favorite favorite = new Favorite(sampleUser, samplePlace);
        favorite.setId(UUID.randomUUID());
        favorite.onCreate();

        when(favoriteRepository.findByUserIdAndPlaceStatus(eq(userId), eq(PlaceStatus.PUBLISHED), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(favorite)));

        PageResponse<FavoriteItemResponse> result = favoriteService.listFavorites(userId, 0, 20);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).place().name()).isEqualTo("Bãi biển Mỹ Khê");
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw InvalidQueryParamException for negative page in listFavorites")
    void shouldThrowForNegativePage() {
        assertThatThrownBy(() -> favoriteService.listFavorites(userId, -1, 20))
                .isInstanceOf(InvalidQueryParamException.class)
                .hasMessageContaining("'page' không được âm");
    }

    @Test
    @DisplayName("Should throw InvalidQueryParamException for invalid size in listFavorites")
    void shouldThrowForInvalidSize() {
        assertThatThrownBy(() -> favoriteService.listFavorites(userId, 0, 0))
                .isInstanceOf(InvalidQueryParamException.class)
                .hasMessageContaining("'size' phải nằm trong khoảng từ 1 đến 100");

        assertThatThrownBy(() -> favoriteService.listFavorites(userId, 0, 105))
                .isInstanceOf(InvalidQueryParamException.class)
                .hasMessageContaining("'size' phải nằm trong khoảng từ 1 đến 100");
    }

    @Test
    @DisplayName("Should check if place is favorited")
    void shouldCheckIsPlaceFavorited() {
        when(favoriteRepository.existsByUserIdAndPlaceId(userId, placeId))
                .thenReturn(true);

        boolean result = favoriteService.isPlaceFavorited(userId, placeId);

        assertThat(result).isTrue();
    }
}
