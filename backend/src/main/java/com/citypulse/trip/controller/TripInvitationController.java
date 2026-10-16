package com.citypulse.trip.controller;

import com.citypulse.common.response.ApiMessage;
import com.citypulse.trip.service.InvitationResponse;
import com.citypulse.trip.service.SendInvitationRequest;
import com.citypulse.trip.service.TripInvitationService;
import com.citypulse.user.service.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller cho luồng Lời mời tham gia chuyến đi (UC-01 -> UC-04).
 */
@RestController
public class TripInvitationController {

    private final TripInvitationService invitationService;

    public TripInvitationController(TripInvitationService invitationService) {
        this.invitationService = invitationService;
    }

    /**
     * UC-01: POST /api/v1/trips/{tripId}/invitations
     * Gửi lời mời tham gia chuyến đi.
     */
    @PostMapping("/api/v1/trips/{tripId}/invitations")
    public ResponseEntity<?> sendInvitation(
            @PathVariable Long tripId,
            @AuthenticationPrincipal CurrentUser currentUser,
            @Valid @RequestBody SendInvitationRequest request) {

        try {
            InvitationResponse response = invitationService.sendInvitation(
                    tripId, currentUser.id(), request.email());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (DataIntegrityViolationException ex) {
            // Race condition: DB unique index đã bắt duplicate pending invitation
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiMessage("Lời mời đã được gửi trước đó."));
        }
    }

    /**
     * UC-02: Chấp nhận lời mời qua Token
     * POST /api/v1/invitations/{token}/accept
     */
    @PostMapping("/api/v1/invitations/{token}/accept")
    public ResponseEntity<InvitationResponse> acceptInvitationByToken(
            @PathVariable String token,
            @AuthenticationPrincipal CurrentUser currentUser) {
        InvitationResponse response = invitationService.acceptInvitationByToken(token, currentUser.id());
        return ResponseEntity.ok(response);
    }

    /**
     * UC-02: Chấp nhận lời mời theo Trip ID và Invitation ID
     * POST /api/v1/trips/{tripId}/invitations/{invitationId}/accept
     */
    @PostMapping("/api/v1/trips/{tripId}/invitations/{invitationId}/accept")
    public ResponseEntity<InvitationResponse> acceptInvitation(
            @PathVariable Long tripId,
            @PathVariable Long invitationId,
            @AuthenticationPrincipal CurrentUser currentUser) {
        InvitationResponse response = invitationService.acceptInvitation(tripId, invitationId, currentUser.id());
        return ResponseEntity.ok(response);
    }

    /**
     * UC-03: Từ chối lời mời qua Token
     * POST /api/v1/invitations/{token}/decline
     */
    @PostMapping("/api/v1/invitations/{token}/decline")
    public ResponseEntity<InvitationResponse> declineInvitationByToken(
            @PathVariable String token,
            @AuthenticationPrincipal CurrentUser currentUser) {
        InvitationResponse response = invitationService.declineInvitationByToken(token, currentUser.id());
        return ResponseEntity.ok(response);
    }

    /**
     * UC-03: Từ chối lời mời theo Trip ID và Invitation ID
     * POST /api/v1/trips/{tripId}/invitations/{invitationId}/decline
     */
    @PostMapping("/api/v1/trips/{tripId}/invitations/{invitationId}/decline")
    public ResponseEntity<InvitationResponse> declineInvitation(
            @PathVariable Long tripId,
            @PathVariable Long invitationId,
            @AuthenticationPrincipal CurrentUser currentUser) {
        InvitationResponse response = invitationService.declineInvitation(tripId, invitationId, currentUser.id());
        return ResponseEntity.ok(response);
    }

    /**
     * UC-04: Hủy lời mời đã gửi (chỉ Owner)
     * DELETE /api/v1/trips/{tripId}/invitations/{invitationId}
     */
    @DeleteMapping("/api/v1/trips/{tripId}/invitations/{invitationId}")
    public ResponseEntity<InvitationResponse> cancelInvitation(
            @PathVariable Long tripId,
            @PathVariable Long invitationId,
            @AuthenticationPrincipal CurrentUser currentUser) {
        InvitationResponse response = invitationService.cancelInvitation(tripId, invitationId, currentUser.id());
        return ResponseEntity.ok(response);
    }

    /**
     * UC-04: Hủy lời mời (endpoint POST thay thế)
     * POST /api/v1/trips/{tripId}/invitations/{invitationId}/cancel
     */
    @PostMapping("/api/v1/trips/{tripId}/invitations/{invitationId}/cancel")
    public ResponseEntity<InvitationResponse> cancelInvitationPost(
            @PathVariable Long tripId,
            @PathVariable Long invitationId,
            @AuthenticationPrincipal CurrentUser currentUser) {
        InvitationResponse response = invitationService.cancelInvitation(tripId, invitationId, currentUser.id());
        return ResponseEntity.ok(response);
    }
}
