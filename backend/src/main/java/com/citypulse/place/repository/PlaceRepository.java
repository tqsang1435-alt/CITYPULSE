package com.citypulse.place.repository;

import com.citypulse.place.entity.Place;
import com.citypulse.place.entity.PlaceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlaceRepository extends JpaRepository<Place, UUID>, JpaSpecificationExecutor<Place> {

    Optional<Place> findByIdAndStatus(UUID id, PlaceStatus status);

    Optional<Place> findBySlugAndStatus(String slug, PlaceStatus status);

    Optional<Place> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
