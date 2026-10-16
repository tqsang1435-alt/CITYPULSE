package com.citypulse.trip.controller;

import com.citypulse.common.response.ApiMessage;
import com.citypulse.trip.service.MemberResponse;
import com.citypulse.trip.service.TripMembersListResponse;
import com.citypulse.trip.service.TripMembershipService;
import com.citypulse.trip.service.UpdateRoleRequest;
import com.citypulse.user.service.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller cho luồng Quản lý thành viên chuyến đi (UC-07, UC-08).
 */
@RestController
@RequestMapping("/api/v1/trips/{tripId}/members")
public class TripMembershipController {

    private final TripMembershipService membershipService;

    public TripMembershipController(TripMembershipService membershipService) {
        this.membershipService = membershipService;
    }

    /**
     * UC-07: Xem danh sách thành viên và lời mời pending
     * GET /api/v1/trips/{tripId}/members
     */
    @GetMapping
    public ResponseEntity<TripMembersListResponse> getMembers(
            @PathVariable Long tripId,
            @AuthenticationPrincipal CurrentUser currentUser) {
        TripMembersListResponse response = membershipService.getMembers(tripId, currentUser.id());
        return ResponseEntity.ok(response);
    }

    /**
     * UC-08: Thay đổi vai trò thành viên (chỉ Owner)
     * PATCH /api/v1/trips/{tripId}/members/{userId}/role
     */
    @PatchMapping("/{userId}/role")
    public ResponseEntity<MemberResponse> updateMemberRole(
            @PathVariable Long tripId,
            @PathVariable Long userId,
            @AuthenticationPrincipal CurrentUser currentUser,
            @Valid @RequestBody UpdateRoleRequest request) {
        MemberResponse response = membershipService.updateMemberRole(tripId, userId, request.role(), currentUser.id());
        return ResponseEntity.ok(response);
    }

    /**
     * UC-08: Xóa thành viên khỏi chuyến đi (chỉ Owner hoặc tự rời đi)
     * DELETE /api/v1/trips/{tripId}/members/{userId}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiMessage> removeMember(
            @PathVariable Long tripId,
            @PathVariable Long userId,
            @AuthenticationPrincipal CurrentUser currentUser) {
        membershipService.removeMember(tripId, userId, currentUser.id());
        return ResponseEntity.ok(new ApiMessage("Đã xóa thành viên khỏi chuyến đi."));
    }
}

