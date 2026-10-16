package com.citypulse.trip.entity;

import com.citypulse.user.entity.AppUser;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Lời mời tham gia chuyến đi.
 *
 * <p>Ràng buộc tránh duplicate pending:
 * - Ở tầng DB: partial unique index {@code uidx_trip_invitation_pending}
 *   được định nghĩa trong migration V2.
 * - Ở tầng Service: kiểm tra bằng {@code existsPendingForTripAndEmail}
 *   trước khi insert, bên trong @Transactional để tránh race condition thông thường.
 */
@Entity
@Table(name = "trip_invitation")
public class TripInvitation {

    /** Các trạng thái hợp lệ — phải khớp với CHECK constraint trong migration. */
    public enum Status {
        pending, accepted, declined, expired, cancelled
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invited_by_id", nullable = false)
    private AppUser invitedBy;

    @Column(name = "invitee_email", nullable = false, length = 254)
    private String inviteeEmail;

    @Column(name = "token", length = 64, unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected TripInvitation() {}

    public TripInvitation(Trip trip, AppUser invitedBy, String inviteeEmail, Instant expiresAt) {
        this(trip, invitedBy, inviteeEmail, expiresAt, UUID.randomUUID().toString());
    }

    public TripInvitation(Trip trip, AppUser invitedBy, String inviteeEmail, Instant expiresAt, String token) {
        this.trip         = trip;
        this.invitedBy    = invitedBy;
        this.inviteeEmail = inviteeEmail;
        this.status       = Status.pending;
        this.expiresAt    = expiresAt;
        this.token        = (token != null && !token.isBlank()) ? token : UUID.randomUUID().toString();
        this.createdAt    = Instant.now();
        this.updatedAt    = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public boolean isPending() {
        return this.status == Status.pending;
    }

    public void accept() {
        this.status = Status.accepted;
        this.updatedAt = Instant.now();
    }

    public void decline() {
        this.status = Status.declined;
        this.updatedAt = Instant.now();
    }

    public void expire() {
        this.status = Status.expired;
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        this.status = Status.cancelled;
        this.updatedAt = Instant.now();
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public Long getId()              { return id; }
    public Trip getTrip()            { return trip; }
    public AppUser getInvitedBy()    { return invitedBy; }
    public String getInviteeEmail()  { return inviteeEmail; }
    public String getToken()         { return token; }
    public Status getStatus()        { return status; }
    public Instant getExpiresAt()    { return expiresAt; }
    public Instant getCreatedAt()    { return createdAt; }
    public Instant getUpdatedAt()    { return updatedAt; }
}
