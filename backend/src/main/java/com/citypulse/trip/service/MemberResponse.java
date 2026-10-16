package com.citypulse.trip.service;

import com.citypulse.trip.entity.TripMembership;
import com.citypulse.trip.entity.TripRole;
import java.time.Instant;

public record MemberResponse(
        Long membershipId,
        Long userId,
        String email,
        String displayName,
        TripRole role,
        Instant joinedAt
) {
    public static MemberResponse from(TripMembership m) {
        return new MemberResponse(
                m.getId(),
                m.getUser().getId(),
                m.getUser().getEmail(),
                m.getUser().getDisplayName(),
                m.getRole(),
                m.getJoinedAt()
        );
    }
}

