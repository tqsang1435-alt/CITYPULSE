package com.citypulse.trip.service;

import com.citypulse.trip.entity.TripInvitation;
import java.time.Instant;

/**
 * Response trả về sau khi tạo Invitation thành công hoặc cập nhật lời mời.
 */
public record InvitationResponse(
        Long id,
        Long tripId,
        String tripTitle,
        String inviteeEmail,
        String status,
        String token,
        Instant expiresAt,
        Instant createdAt
) {
    /** Constructor tương thích ngược cho 7 tham số */
    public InvitationResponse(Long id, Long tripId, String tripTitle, String inviteeEmail, String status, Instant expiresAt, Instant createdAt) {
        this(id, tripId, tripTitle, inviteeEmail, status, null, expiresAt, createdAt);
    }

    /** Factory method để tạo response từ entity */
    public static InvitationResponse from(TripInvitation inv) {
        return new InvitationResponse(
                inv.getId(),
                inv.getTrip().getId(),
                inv.getTrip().getTitle(),
                inv.getInviteeEmail(),
                inv.getStatus().name(),
                inv.getToken(),
                inv.getExpiresAt(),
                inv.getCreatedAt()
        );
    }
}
