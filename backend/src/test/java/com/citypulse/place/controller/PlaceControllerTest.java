package com.citypulse.place.controller;

import com.citypulse.common.dto.PageResponse;
import com.citypulse.common.exception.GlobalExceptionHandler;
import com.citypulse.common.exception.InvalidQueryParamException;
import com.citypulse.common.exception.PlaceNotFoundException;
import com.citypulse.place.dto.PlaceDetailResponse;
import com.citypulse.place.dto.PlaceSummaryResponse;
import com.citypulse.place.entity.PlaceStatus;
import com.citypulse.place.service.PlaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PlaceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PlaceService placeService;

    @InjectMocks
    private PlaceController placeController;

    private UUID sampleId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(placeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        sampleId = UUID.randomUUID();
    }

    @Test
    @DisplayName("GET /api/v1/places - Should return 200 and paginated list of places")
    void shouldReturn200ForListPlaces() throws Exception {
        PlaceSummaryResponse summary = new PlaceSummaryResponse(
            sampleId,
            "bai-bien-my-khe",
            "Bãi biển Mỹ Khê",
            "BEACH",
            "Quận Sơn Trà",
            "Võ Nguyên Giáp, Sơn Trà",
            16.0597,
            108.2435,
            "/images/my-khe.png",
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            120,
            BigDecimal.valueOf(4.80),
            1300,
            "Biển đẹp, Tắm biển",
            "Tháng 3 – 8"
        );

        PageResponse<PlaceSummaryResponse> pageResponse = new PageResponse<>(
            List.of(summary),
            0,
            20,
            1,
            1,
            true,
            true
        );

        when(placeService.listPublishedPlaces(anyInt(), anyInt(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/places")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].slug").value("bai-bien-my-khe"))
                .andExpect(jsonPath("$.content[0].name").value("Bãi biển Mỹ Khê"))
                .andExpect(jsonPath("$.content[0].district").value("Quận Sơn Trà"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/places with filter params - Should pass filters and return 200")
    void shouldFilterPlacesSuccessfully() throws Exception {
        when(placeService.listPublishedPlaces(
                eq(0), eq(10), eq("biển"), eq("BEACH"), eq("Sơn Trà"), any(), any(), any(), eq("rating,desc"), any()
        )).thenReturn(new PageResponse<>(List.of(), 0, 10, 0, 0, true, true));

        mockMvc.perform(get("/api/v1/places")
                .param("page", "0")
                .param("size", "10")
                .param("q", "biển")
                .param("category", "BEACH")
                .param("district", "Sơn Trà")
                .param("sort", "rating,desc")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("GET /api/v1/places - Invalid page should return 400 Bad Request")
    void shouldReturn400ForInvalidPage() throws Exception {
        when(placeService.listPublishedPlaces(eq(-1), anyInt(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new InvalidQueryParamException("Tham số 'page' không được âm."));

        mockMvc.perform(get("/api/v1/places")
                .param("page", "-1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Tham số 'page' không được âm."));
    }

    @Test
    @DisplayName("GET /api/v1/places - Invalid size should return 400 Bad Request")
    void shouldReturn400ForInvalidSize() throws Exception {
        when(placeService.listPublishedPlaces(anyInt(), eq(150), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new InvalidQueryParamException("Tham số 'size' phải nằm trong khoảng từ 1 đến 100."));

        mockMvc.perform(get("/api/v1/places")
                .param("size", "150")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Tham số 'size' phải nằm trong khoảng từ 1 đến 100."));
    }

    @Test
    @DisplayName("GET /api/v1/places - Unsupported sort should return 400 Bad Request")
    void shouldReturn400ForUnsupportedSort() throws Exception {
        when(placeService.listPublishedPlaces(anyInt(), anyInt(), any(), any(), any(), any(), any(), any(), eq("invalid_field"), any()))
                .thenThrow(new InvalidQueryParamException("Tham số 'sort' không hợp lệ: 'invalid_field'."));

        mockMvc.perform(get("/api/v1/places")
                .param("sort", "invalid_field")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Tham số 'sort' không hợp lệ: 'invalid_field'."));
    }

    @Test
    @DisplayName("GET /api/v1/places/{slug} - Should return 200 and place details")
    void shouldReturn200ForPlaceDetailBySlug() throws Exception {
        PlaceDetailResponse detail = new PlaceDetailResponse(
            sampleId,
            "cau-rong",
            "Cầu Rồng",
            "CULTURE",
            "Quận Hải Châu",
            "Nguyễn Văn Linh, Hải Châu",
            16.0611,
            108.2272,
            PlaceStatus.PUBLISHED,
            "Cầu biểu tượng Đà Nẵng",
            "Mô tả chi tiết Cầu Rồng bắc qua sông Hàn",
            "/images/cau-rong.png",
            "/images/cau-rong.png",
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            60,
            BigDecimal.valueOf(4.90),
            3100,
            "Biểu tượng Đà Nẵng, Phun lửa",
            "Thứ 7 & Chủ Nhật",
            "VERIFIED",
            Instant.now(),
            Instant.now()
        );

        when(placeService.getPlaceByIdOrSlug("cau-rong")).thenReturn(detail);

        mockMvc.perform(get("/api/v1/places/cau-rong")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("cau-rong"))
                .andExpect(jsonPath("$.name").value("Cầu Rồng"))
                .andExpect(jsonPath("$.category").value("CULTURE"))
                .andExpect(jsonPath("$.status").value("PUBLISHED"))
                .andExpect(jsonPath("$.ratingAverage").value(4.90));
    }

    @Test
    @DisplayName("GET /api/v1/places/{id} - Should return 200 for place detail by UUID")
    void shouldReturn200ForPlaceDetailByUuid() throws Exception {
        PlaceDetailResponse detail = new PlaceDetailResponse(
            sampleId,
            "cho-han",
            "Chợ Hàn",
            "FOOD",
            "Quận Hải Châu",
            "119 Trần Phú, Hải Châu",
            16.0682,
            108.2246,
            PlaceStatus.PUBLISHED,
            "Chợ truyền thống lâu đời",
            "Bạt ngàn đặc sản miền Trung",
            "/images/cho-han.jpg",
            "/images/cho-han.jpg",
            BigDecimal.ZERO,
            null,
            90,
            BigDecimal.valueOf(4.60),
            1900,
            "Đặc sản, Chả bò",
            "Cả ngày",
            "VERIFIED",
            Instant.now(),
            Instant.now()
        );

        when(placeService.getPlaceByIdOrSlug(sampleId.toString())).thenReturn(detail);

        mockMvc.perform(get("/api/v1/places/{id}", sampleId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleId.toString()))
                .andExpect(jsonPath("$.name").value("Chợ Hàn"));
    }

    @Test
    @DisplayName("GET /api/v1/places/{idOrSlug} - Should return 404 when place does not exist or not published")
    void shouldReturn404WhenPlaceNotFound() throws Exception {
        when(placeService.getPlaceByIdOrSlug("unknown-place"))
                .thenThrow(new PlaceNotFoundException("Địa điểm không tồn tại hoặc chưa được công khai."));

        mockMvc.perform(get("/api/v1/places/unknown-place")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Địa điểm không tồn tại hoặc chưa được công khai."));
    }
}
