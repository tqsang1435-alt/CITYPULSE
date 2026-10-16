package com.citypulse.trip.repository;

import com.citypulse.trip.entity.Trip;
import com.citypulse.trip.entity.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<Trip, UUID> {

    List<Trip> findAllByOwnerIdOrderByStartDateDesc(UUID ownerId);

    List<Trip> findAllByOwnerIdAndStatusOrderByStartDateDesc(UUID ownerId, TripStatus status);

    Optional<Trip> findByIdAndOwnerId(UUID id, UUID ownerId);

    long countByOwnerId(UUID ownerId);

    long countByOwnerIdAndStatus(UUID ownerId, TripStatus status);

    @Query("SELECT t FROM Trip t LEFT JOIN FETCH t.stops WHERE t.id = :id")
    Optional<Trip> findByIdWithStops(@Param("id") UUID id);

    @Query("SELECT t FROM Trip t LEFT JOIN FETCH t.stops WHERE t.id = :id AND t.ownerId = :ownerId")
    Optional<Trip> findByIdAndOwnerIdWithStops(@Param("id") UUID id, @Param("ownerId") UUID ownerId);
}

