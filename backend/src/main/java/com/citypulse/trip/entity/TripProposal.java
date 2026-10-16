package com.citypulse.trip.entity;

import com.citypulse.user.entity.AppUser;
import jakarta.persistence.*;
import java.time.Instant;

/**
 * Đề xuất địa điểm trong chuyến đi (UC-05, UC-06).
 */
@Entity
@Table(name = "trip_proposal")
public class TripProposal {

    public enum Status {
        pending, approved, rejected
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proposed_by_id", nullable = false)
    private AppUser proposedBy;

    @Column(name = "place_id")
    private Long placeId;

    @Column(name = "place_name", nullable = false, length = 200)
    private String placeName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_id")
    private AppUser reviewedBy;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "review_note", length = 500)
    private String reviewNote;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected TripProposal() {}

    public TripProposal(Trip trip, AppUser proposedBy, Long placeId, String placeName, String description) {
        this.trip        = trip;
        this.proposedBy  = proposedBy;
        this.placeId     = placeId;
        this.placeName   = placeName;
        this.description = description;
        this.status      = Status.pending;
        this.createdAt   = Instant.now();
        this.updatedAt   = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public boolean isPending() {
        return this.status == Status.pending;
    }

    public void approve(AppUser reviewer, String note) {
        this.status     = Status.approved;
        this.reviewedBy = reviewer;
        this.reviewedAt = Instant.now();
        this.reviewNote = note;
        this.updatedAt  = Instant.now();
    }

    public void reject(AppUser reviewer, String note) {
        this.status     = Status.rejected;
        this.reviewedBy = reviewer;
        this.reviewedAt = Instant.now();
        this.reviewNote = note;
        this.updatedAt  = Instant.now();
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public Long getId()              { return id; }
    public Trip getTrip()            { return trip; }
    public AppUser getProposedBy()   { return proposedBy; }
    public Long getPlaceId()         { return placeId; }
    public String getPlaceName()     { return placeName; }
    public String getDescription()   { return description; }
    public Status getStatus()        { return status; }
    public AppUser getReviewedBy()   { return reviewedBy; }
    public Instant getReviewedAt()   { return reviewedAt; }
    public String getReviewNote()    { return reviewNote; }
    public Instant getCreatedAt()    { return createdAt; }
    public Instant getUpdatedAt()    { return updatedAt; }
}

