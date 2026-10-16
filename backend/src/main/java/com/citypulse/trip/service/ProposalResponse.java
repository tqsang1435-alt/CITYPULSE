package com.citypulse.trip.service;

import com.citypulse.trip.entity.TripProposal;
import java.time.Instant;

public record ProposalResponse(
        Long id,
        Long tripId,
        Long placeId,
        String placeName,
        String description,
        String status,
        Long proposedByUserId,
        String proposedByName,
        Long reviewedByUserId,
        String reviewedByName,
        Instant reviewedAt,
        String reviewNote,
        Instant createdAt
) {
    public static ProposalResponse from(TripProposal p) {
        return new ProposalResponse(
                p.getId(),
                p.getTrip().getId(),
                p.getPlaceId(),
                p.getPlaceName(),
                p.getDescription(),
                p.getStatus().name(),
                p.getProposedBy().getId(),
                p.getProposedBy().getDisplayName(),
                p.getReviewedBy() != null ? p.getReviewedBy().getId() : null,
                p.getReviewedBy() != null ? p.getReviewedBy().getDisplayName() : null,
                p.getReviewedAt(),
                p.getReviewNote(),
                p.getCreatedAt()
        );
    }
}

