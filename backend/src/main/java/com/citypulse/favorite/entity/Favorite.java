package com.citypulse.favorite.entity;

import com.citypulse.place.entity.Place;
import com.citypulse.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing a bookmarked/favorited place by a user.
 * Table: favorites
 */
@Entity
@Table(
    name = "favorites",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_favorites_user_place", columnNames = {"user_id", "place_id"})
    },
    indexes = {
        @Index(name = "idx_favorites_user_id", columnList = "user_id"),
        @Index(name = "idx_favorites_place_id", columnList = "place_id"),
        @Index(name = "idx_favorites_created_at", columnList = "created_at")
    }
)
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Favorite() {
    }

    public Favorite(User user, Place place) {
        this.user = user;
        this.place = place;
    }

    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Favorite other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Favorite{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", placeId=" + (place != null ? place.getId() : null) +
                ", createdAt=" + createdAt +
                '}';
    }
}
