package com.citypulse.trip.repository;

import com.citypulse.trip.entity.TripProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripProposalRepository extends JpaRepository<TripProposal, Long> {

    List<TripProposal> findByTripIdOrderByCreatedAtDesc(Long tripId);

    Optional<TripProposal> findByIdAndTripId(Long id, Long tripId);
}

