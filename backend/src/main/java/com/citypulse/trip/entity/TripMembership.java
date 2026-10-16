package com.citypulse.trip.entity;

import com.citypulse.user.entity.AppUser;
import jakarta.persistence.*;
import java.time.Instant;

/**
 * Thành viên tham gia chuyến đi và vai trò (Owner, Contributor, Viewer).
 */
@Entity
@Table(
    name = "trip_membership",
    uniqueConstraints = {
        @UniqueConstraint(name = "uidx_trip_membership_user", columnNames = {"trip_id", "user_id"})
    }
)
public class TripMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TripRole role;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected TripMembership() {}

    public TripMembership(Trip trip, AppUser user, TripRole role) {
        this.trip      = trip;
        this.user      = user;
        this.role      = role;
        this.joinedAt  = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void setRole(TripRole role) {
        this.role = role;
        this.updatedAt = Instant.now();
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public Long getId()          { return id; }
    public Trip getTrip()        { return trip; }
    public AppUser getUser()     { return user; }
    public TripRole getRole()    { return role; }
    public Instant getJoinedAt() { return joinedAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}

