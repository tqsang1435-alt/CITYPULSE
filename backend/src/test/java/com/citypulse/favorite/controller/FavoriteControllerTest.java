package com.citypulse.favorite.controller;

import com.citypulse.common.dto.PageResponse;
import com.citypulse.common.exception.GlobalExceptionHandler;
import com.citypulse.common.exception.InvalidQueryParamException;
import com.citypulse.common.exception.PlaceNotFoundException;
import com.citypulse.favorite.dto.FavoriteItemResponse;
import com.citypulse.favorite.dto.FavoriteToggleResponse;
import com.citypulse.favorite.service.FavoriteService;
import com.citypulse.place.dto.PlaceSummaryResponse;
import com.citypulse.security.UserPrincipal;
import com.citypulse.user.entity.Role;
import com.citypulse.user.entity.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FavoriteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FavoriteService favoriteService;

    @InjectMocks
    private FavoriteController favoriteController;

    private UUID userId;
    private UUID placeId;
    private UserPrincipal testPrincipal;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        placeId = UUID.randomUUID();
        testPrincipal = new UserPrincipal(
            userId, "user@danangvista.vn", "hash", "Demo User", Role.USER, UserStatus.ACTIVE
        );

        // Custom argument resolver to inject testPrincipal into @AuthenticationPrincipal
        HandlerMethodArgumentResolver principalResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.getParameterType().isAssignableFrom(UserPrincipal.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                return testPrincipal;
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(favoriteController)
                .setCustomArgumentResolvers(principalResolver)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/favorites/{placeId} - Should add favorite and return 201 Created")
    void shouldAddFavorite() throws Exception {
        when(favoriteService.addFavorite(eq(userId), eq(placeId.toString())))
                .thenReturn(new FavoriteToggleResponse(placeId, true, "Đã thêm vào danh sách yêu thích thành công."));

        mockMvc.perform(post("/api/v1/favorites/{placeId}", placeId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placeId").value(placeId.toString()))
                .andExpect(jsonPath("$.isFavorite").value(true))
                .andExpect(jsonPath("$.message").value("Đã thêm vào danh sách yêu thích thành công."));
    }

    @Test
    @DisplayName("DELETE /api/v1/favorites/{placeId} - Should remove favorite and return 200 OK")
    void shouldRemoveFavorite() throws Exception {
        when(favoriteService.removeFavorite(eq(userId), eq(placeId.toString())))
                .thenReturn(new FavoriteToggleResponse(placeId, false, "Đã xóa khỏi danh sách yêu thích thành công."));

        mockMvc.perform(delete("/api/v1/favorites/{placeId}", placeId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placeId").value(placeId.toString()))
                .andExpect(jsonPath("$.isFavorite").value(false))
                .andExpect(jsonPath("$.message").value("Đã xóa khỏi danh sách yêu thích thành công."));
    }

    @Test
    @DisplayName("GET /api/v1/favorites - Should return 200 OK with paginated favorites")
    void shouldListFavorites() throws Exception {
        PlaceSummaryResponse summary = new PlaceSummaryResponse(
            placeId, "bai-bien-my-khe", "Bãi biển Mỹ Khê", "BEACH", "Quận Sơn Trà",
            "Võ Nguyên Giáp", 16.0597, 108.2435, "/images/my-khe.png",
            BigDecimal.ZERO, BigDecimal.ZERO, 120, BigDecimal.valueOf(4.80),
            1300, "Biển", "Sáng"
        );

        FavoriteItemResponse item = new FavoriteItemResponse(
            UUID.randomUUID(), Instant.now(), summary
        );

        PageResponse<FavoriteItemResponse> pageResponse = new PageResponse<>(
            List.of(item), 0, 20, 1, 1, true, true
        );

        when(favoriteService.listFavorites(eq(userId), eq(0), eq(20)))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/favorites")
                .param("page", "0")
                .param("size", "20")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].place.name").value("Bãi biển Mỹ Khê"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("POST /api/v1/favorites/{placeId} - Place not found returns 404")
    void shouldReturn404WhenPlaceNotFound() throws Exception {
        when(favoriteService.addFavorite(eq(userId), eq("non-existent-place")))
                .thenThrow(new PlaceNotFoundException("Địa điểm không tồn tại hoặc chưa được công khai."));

        mockMvc.perform(post("/api/v1/favorites/non-existent-place")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Địa điểm không tồn tại hoặc chưa được công khai."));
    }

    @Test
    @DisplayName("GET /api/v1/favorites - Invalid page returns 400 Bad Request")
    void shouldReturn400WhenPageIsNegative() throws Exception {
        when(favoriteService.listFavorites(eq(userId), eq(-1), anyInt()))
                .thenThrow(new InvalidQueryParamException("Tham số 'page' không được âm."));

        mockMvc.perform(get("/api/v1/favorites")
                .param("page", "-1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Tham số 'page' không được âm."));
    }
}
