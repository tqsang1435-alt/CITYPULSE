package com.citypulse.place.service;

import com.citypulse.common.dto.PageResponse;
import com.citypulse.common.exception.InvalidQueryParamException;
import com.citypulse.common.exception.PlaceNotFoundException;
import com.citypulse.place.dto.PlaceDetailResponse;
import com.citypulse.place.dto.PlaceSummaryResponse;
import com.citypulse.place.entity.Place;
import com.citypulse.place.entity.PlaceStatus;
import com.citypulse.place.repository.PlaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private PlaceService placeService;

    private Place samplePlace;
    private UUID sampleId;

    @BeforeEach
    void setUp() {
        sampleId = UUID.randomUUID();
        samplePlace = new Place("bai-bien-my-khe", "Bãi biển Mỹ Khê", "BEACH", "Quận Sơn Trà");
        samplePlace.setId(sampleId);
        samplePlace.setAddress("Võ Nguyên Giáp, Sơn Trà, Đà Nẵng");
        samplePlace.setLatitude(16.0597);
        samplePlace.setLongitude(108.2435);
        samplePlace.setStatus(PlaceStatus.PUBLISHED);
        samplePlace.setShortDescription("Bãi cát trắng mịn trải dài");
        samplePlace.setPriceMin(BigDecimal.ZERO);
        samplePlace.setPriceMax(BigDecimal.ZERO);
        samplePlace.setRatingAverage(BigDecimal.valueOf(4.80));
        samplePlace.setReviewCount(1300);
        samplePlace.setVisitDurationMin(120);
    }

    @Test
    @DisplayName("Should list published places with pagination metadata")
    void shouldListPublishedPlaces() {
        when(placeRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(samplePlace)));

        PageResponse<PlaceSummaryResponse> result = placeService.listPublishedPlaces(
                0, 20, null, null, null, null, null, null, "rating,desc", false
        );

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).slug()).isEqualTo("bai-bien-my-khe");
        assertThat(result.content().get(0).ratingAverage()).isEqualTo(BigDecimal.valueOf(4.80));
        assertThat(result.page()).isEqualTo(0);
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw InvalidQueryParamException when page is negative")
    void shouldThrowWhenPageNegative() {
        assertThatThrownBy(() -> placeService.listPublishedPlaces(
                -1, 20, null, null, null, null, null, null, null, null
        )).isInstanceOf(InvalidQueryParamException.class)
          .hasMessageContaining("'page' không được âm");
    }

    @Test
    @DisplayName("Should throw InvalidQueryParamException when size is out of bounds")
    void shouldThrowWhenSizeOutOfBounds() {
        assertThatThrownBy(() -> placeService.listPublishedPlaces(
                0, 0, null, null, null, null, null, null, null, null
        )).isInstanceOf(InvalidQueryParamException.class)
          .hasMessageContaining("'size' phải nằm trong khoảng từ 1 đến 100");

        assertThatThrownBy(() -> placeService.listPublishedPlaces(
                0, 101, null, null, null, null, null, null, null, null
        )).isInstanceOf(InvalidQueryParamException.class)
          .hasMessageContaining("'size' phải nằm trong khoảng từ 1 đến 100");
    }

    @Test
    @DisplayName("Should throw InvalidQueryParamException when minPrice is negative")
    void shouldThrowWhenMinPriceNegative() {
        assertThatThrownBy(() -> placeService.listPublishedPlaces(
                0, 20, null, null, null, BigDecimal.valueOf(-1000), null, null, null, null
        )).isInstanceOf(InvalidQueryParamException.class)
          .hasMessageContaining("'minPrice' không được âm");
    }

    @Test
    @DisplayName("Should throw InvalidQueryParamException when maxPrice is lower than minPrice")
    void shouldThrowWhenMaxPriceLowerThanMinPrice() {
        assertThatThrownBy(() -> placeService.listPublishedPlaces(
                0, 20, null, null, null, BigDecimal.valueOf(500000), BigDecimal.valueOf(200000), null, null, null
        )).isInstanceOf(InvalidQueryParamException.class)
          .hasMessageContaining("'maxPrice' phải lớn hơn hoặc bằng 'minPrice'");
    }

    @Test
    @DisplayName("Should throw InvalidQueryParamException when minRating is out of 0-5 range")
    void shouldThrowWhenMinRatingInvalid() {
        assertThatThrownBy(() -> placeService.listPublishedPlaces(
                0, 20, null, null, null, null, null, 6.0, null, null
        )).isInstanceOf(InvalidQueryParamException.class)
          .hasMessageContaining("'minRating' phải nằm trong khoảng từ 0.0 đến 5.0");
    }

    @Test
    @DisplayName("Should throw InvalidQueryParamException for unsupported sort field")
    void shouldThrowForUnsupportedSortField() {
        assertThatThrownBy(() -> placeService.listPublishedPlaces(
                0, 20, null, null, null, null, null, null, "hack_column; drop table", null
        )).isInstanceOf(InvalidQueryParamException.class)
          .hasMessageContaining("Tham số 'sort' không hợp lệ");
    }

    @Test
    @DisplayName("Should map supported sort fields correctly")
    void shouldMapSupportedSortFields() {
        when(placeRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(samplePlace)));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

        // Sort by name ASC
        placeService.listPublishedPlaces(0, 10, null, null, null, null, null, null, "name,asc", null);
        verify(placeRepository).findAll(any(Specification.class), captor.capture());
        Sort.Order order = captor.getValue().getSort().getOrderFor("name");
        assertThat(order).isNotNull();
        assertThat(order.getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    @DisplayName("Should retrieve published place by slug")
    void shouldRetrievePlaceBySlug() {
        when(placeRepository.findBySlugAndStatus("bai-bien-my-khe", PlaceStatus.PUBLISHED))
                .thenReturn(Optional.of(samplePlace));

        PlaceDetailResponse detail = placeService.getPlaceByIdOrSlug("bai-bien-my-khe");

        assertThat(detail).isNotNull();
        assertThat(detail.slug()).isEqualTo("bai-bien-my-khe");
        assertThat(detail.name()).isEqualTo("Bãi biển Mỹ Khê");
        assertThat(detail.status()).isEqualTo(PlaceStatus.PUBLISHED);
    }

    @Test
    @DisplayName("Should retrieve published place by UUID")
    void shouldRetrievePlaceByUuid() {
        when(placeRepository.findByIdAndStatus(sampleId, PlaceStatus.PUBLISHED))
                .thenReturn(Optional.of(samplePlace));

        PlaceDetailResponse detail = placeService.getPlaceByIdOrSlug(sampleId.toString());

        assertThat(detail).isNotNull();
        assertThat(detail.id()).isEqualTo(sampleId);
        assertThat(detail.name()).isEqualTo("Bãi biển Mỹ Khê");
    }

    @Test
    @DisplayName("Should throw PlaceNotFoundException when place not found")
    void shouldThrowWhenPlaceNotFound() {
        when(placeRepository.findBySlugAndStatus("non-existent-place", PlaceStatus.PUBLISHED))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> placeService.getPlaceByIdOrSlug("non-existent-place"))
                .isInstanceOf(PlaceNotFoundException.class)
                .hasMessageContaining("Địa điểm không tồn tại hoặc chưa được công khai");
    }

    @Test
    @DisplayName("Should throw PlaceNotFoundException for blank identifier")
    void shouldThrowWhenIdentifierIsBlank() {
        assertThatThrownBy(() -> placeService.getPlaceByIdOrSlug("   "))
                .isInstanceOf(PlaceNotFoundException.class)
                .hasMessageContaining("Địa điểm không tồn tại hoặc chưa được công khai");
    }
}
