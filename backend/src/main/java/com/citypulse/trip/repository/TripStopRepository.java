package com.citypulse.trip.repository;

import com.citypulse.trip.entity.TripStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TripStopRepository extends JpaRepository<TripStop, UUID> {

    List<TripStop> findAllByTripIdOrderByDayNumberAscOrderIndexAsc(UUID tripId);

    List<TripStop> findAllByTripIdAndDayNumberOrderByOrderIndexAsc(UUID tripId, Integer dayNumber);

    Optional<TripStop> findByIdAndTripId(UUID id, UUID tripId);

    long countByTripId(UUID tripId);

    @Query("SELECT COALESCE(MAX(s.orderIndex), -1) FROM TripStop s WHERE s.trip.id = :tripId AND s.dayNumber = :dayNumber")
    int findMaxOrderIndexByTripIdAndDayNumber(@Param("tripId") UUID tripId, @Param("dayNumber") Integer dayNumber);

    void deleteAllByTripId(UUID tripId);
}

