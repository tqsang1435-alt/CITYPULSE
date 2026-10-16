package com.citypulse.trip.entity;

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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a planned stop within a Trip.
 * Table: trip_stops
 *
 * NOTE ON TV1 DECOUPLING:
 * placeId is stored as a UUID column without direct JPA foreign key mapping to Place
 * to enable independent development before TV1's Place entity is finalized and merged.
 */
@Entity
@Table(
    name = "trip_stops",
    indexes = {
        @Index(name = "idx_trip_stops_trip", columnList = "trip_id"),
        @Index(name = "idx_trip_stops_day_order", columnList = "trip_id, day_number, order_index")
    }
)
public class TripStop {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(name = "place_id")
    private UUID placeId;

    @Column(name = "custom_name", length = 255)
    private String customName;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber = 1;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex = 0;

    @Column(name = "arrival_time")
    private LocalTime arrivalTime;

    @Column(name = "departure_time")
    private LocalTime departureTime;

    @Column(name = "estimated_cost", precision = 12, scale = 2)
    private BigDecimal estimatedCost;

    @Column(name = "note", length = 1000)
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public TripStop() {
    }

    public TripStop(Trip trip, String customName, Integer dayNumber, Integer orderIndex) {
        this.trip = trip;
        this.customName = customName;
        this.dayNumber = dayNumber != null ? dayNumber : 1;
        this.orderIndex = orderIndex != null ? orderIndex : 0;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
        if (this.dayNumber == null || this.dayNumber < 1) {
            this.dayNumber = 1;
        }
        if (this.orderIndex == null || this.orderIndex < 0) {
            this.orderIndex = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // Business validations

    /**
     * Checks if coordinates are in legal world ranges if specified.
     */
    public boolean areCoordinatesValid() {
        if (latitude != null && (latitude < -90.0 || latitude > 90.0)) {
            return false;
        }
        if (longitude != null && (longitude < -180.0 || longitude > 180.0)) {
            return false;
        }
        return true;
    }

    /**
     * Checks if arrival time is before or equal to departure time if both specified.
     */
    public boolean areTimesValid() {
        if (arrivalTime != null && departureTime != null) {
            return !arrivalTime.isAfter(departureTime);
        }
        return true;
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public UUID getPlaceId() {
        return placeId;
    }

    public void setPlaceId(UUID placeId) {
        this.placeId = placeId;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(Integer dayNumber) {
        this.dayNumber = dayNumber;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(BigDecimal estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TripStop other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "TripStop{" +
                "id=" + id +
                ", customName='" + customName + '\'' +
                ", dayNumber=" + dayNumber +
                ", orderIndex=" + orderIndex +
                ", arrivalTime=" + arrivalTime +
                ", departureTime=" + departureTime +
                ", createdAt=" + createdAt +
                '}';
    }
}

