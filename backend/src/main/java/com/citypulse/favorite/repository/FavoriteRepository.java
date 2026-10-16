package com.citypulse.favorite.repository;

import com.citypulse.favorite.entity.Favorite;
import com.citypulse.place.entity.PlaceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {

    Optional<Favorite> findByUserIdAndPlaceId(UUID userId, UUID placeId);

    boolean existsByUserIdAndPlaceId(UUID userId, UUID placeId);

    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.user.id = :userId AND f.place.id = :placeId")
    void deleteByUserIdAndPlaceId(@Param("userId") UUID userId, @Param("placeId") UUID placeId);

    @Query(
        value = "SELECT f FROM Favorite f JOIN FETCH f.place p WHERE f.user.id = :userId AND p.status = :status",
        countQuery = "SELECT count(f) FROM Favorite f WHERE f.user.id = :userId AND f.place.status = :status"
    )
    Page<Favorite> findByUserIdAndPlaceStatus(
        @Param("userId") UUID userId,
        @Param("status") PlaceStatus status,
        Pageable pageable
    );

    long countByPlaceId(UUID placeId);

    long countByUserId(UUID userId);
}
