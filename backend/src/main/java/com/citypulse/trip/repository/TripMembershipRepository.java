package com.citypulse.trip.repository;

import com.citypulse.trip.entity.TripMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripMembershipRepository extends JpaRepository<TripMembership, Long> {

    List<TripMembership> findByTripIdOrderByJoinedAtAsc(Long tripId);

    Optional<TripMembership> findByTripIdAndUserId(Long tripId, Long userId);

    boolean existsByTripIdAndUserId(Long tripId, Long userId);

    void deleteByTripIdAndUserId(Long tripId, Long userId);

    List<TripMembership> findByUserId(Long userId);
}

