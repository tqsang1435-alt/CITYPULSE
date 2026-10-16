package com.citypulse.trip;

import com.citypulse.common.exception.AccessDeniedException;
import com.citypulse.common.exception.BusinessRuleException;
import com.citypulse.common.exception.ResourceNotFoundException;
import com.citypulse.trip.service.InvitationResponse;
import com.citypulse.trip.service.TripEmailException;
import com.citypulse.trip.service.TripInvitationService;
import com.citypulse.user.service.CurrentUser;
import com.citypulse.user.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-layer tests cho TripInvitationController (UC-01).
 *
 * Dùng @WebMvcTest để chỉ load web layer, mock TripInvitationService.
 */
@WebMvcTest(controllers = com.citypulse.trip.controller.TripInvitationController.class)
@AutoConfigureMockMvc
@DisplayName("UC-01: TripInvitationController")
class TripInvitationControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean TripInvitationService invitationService;
    @MockitoBean AuthService authService;

    private static final String URL = "/api/v1/trips/10/invitations";

    private RequestPostProcessor authenticatedAs(long userId) {
        var principal = new CurrentUser(userId, "owner@example.com", "Owner");
        var authRequest = authentication(
                new UsernamePasswordAuthenticationToken(principal, null, java.util.List.of()));
        return request -> csrf().postProcessRequest(authRequest.postProcessRequest(request));
    }

    // ── 1. Thành công → 201 ───────────────────────────────────────────────────

    @Test
    @DisplayName("POST /trips/{id}/invitations → 201 Created khi thành công")
    void sendInvitation_returns201() throws Exception {
        InvitationResponse resp = new InvitationResponse(
                1L, 10L, "Hành trình Đà Nẵng",
                "member@example.com", "pending",
                Instant.now().plus(7, ChronoUnit.DAYS), Instant.now());

        given(invitationService.sendInvitation(eq(10L), eq(1L), eq("member@example.com")))
                .willReturn(resp);

        mockMvc.perform(post(URL)
                        .with(authenticatedAs(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "member@example.com" }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("pending"))
                .andExpect(jsonPath("$.inviteeEmail").value("member@example.com"));
    }

    // ── 2. Email không hợp lệ → 400 ──────────────────────────────────────────

    @Test
    @DisplayName("POST /trips/{id}/invitations → 400 khi email không hợp lệ")
    void sendInvitation_invalidEmail_returns400() throws Exception {
        mockMvc.perform(post(URL)
                        .with(authenticatedAs(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "not-an-email" }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());

        then(invitationService).shouldHaveNoInteractions();
    }

    // ── 3. Email trống → 400 ──────────────────────────────────────────────────

    @Test
    @DisplayName("POST /trips/{id}/invitations → 400 khi email để trống")
    void sendInvitation_blankEmail_returns400() throws Exception {
        mockMvc.perform(post(URL)
                        .with(authenticatedAs(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "" }
                                """))
                .andExpect(status().isBadRequest());
    }

    // ── 4. Trip không tồn tại → 404 ──────────────────────────────────────────

    @Test
    @DisplayName("POST /trips/{id}/invitations → 404 khi trip không tồn tại")
    void sendInvitation_tripNotFound_returns404() throws Exception {
        given(invitationService.sendInvitation(eq(10L), any(), any()))
                .willThrow(new ResourceNotFoundException("Không tìm thấy chuyến đi"));

        mockMvc.perform(post(URL)
                        .with(authenticatedAs(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "member@example.com" }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Không tìm thấy chuyến đi"));
    }

    // ── 5. Không phải owner → 403 ────────────────────────────────────────────

    @Test
    @DisplayName("POST /trips/{id}/invitations → 403 khi không phải owner")
    void sendInvitation_notOwner_returns403() throws Exception {
        given(invitationService.sendInvitation(any(), any(), any()))
                .willThrow(new AccessDeniedException("Chỉ owner mới có thể gửi lời mời."));

        mockMvc.perform(post(URL)
                        .with(authenticatedAs(2L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "member@example.com" }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Chỉ owner mới có thể gửi lời mời."));
    }

    // ── 6. Duplicate pending → 409 ───────────────────────────────────────────

    @Test
    @DisplayName("POST /trips/{id}/invitations → 409 khi đã có pending invitation")
    void sendInvitation_duplicatePending_returns409() throws Exception {
        given(invitationService.sendInvitation(any(), any(), any()))
                .willThrow(new BusinessRuleException("Lời mời đã được gửi trước đó đến member@example.com"));

        mockMvc.perform(post(URL)
                        .with(authenticatedAs(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "member@example.com" }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("Lời mời đã được gửi trước đó")));
    }

    // ── 7. Email thất bại → 502 ──────────────────────────────────────────────

    @Test
    @DisplayName("POST /trips/{id}/invitations → 502 khi gửi email thất bại")
    void sendInvitation_emailFails_returns500() throws Exception {
        given(invitationService.sendInvitation(any(), any(), any()))
                .willThrow(new TripEmailException("SMTP lỗi", new RuntimeException()));

        mockMvc.perform(post(URL)
                        .with(authenticatedAs(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "member@example.com" }
                                """))
                .andExpect(status().isBadGateway());
    }
}
