package com.citypulse.trip.service;

import java.util.List;

public record TripMembersListResponse(
        Long tripId,
        String tripTitle,
        List<MemberResponse> members,
        List<InvitationResponse> pendingInvitations
) {}

