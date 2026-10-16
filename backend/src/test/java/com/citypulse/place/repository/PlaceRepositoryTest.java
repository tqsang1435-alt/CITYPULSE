package com.citypulse.place.repository;

import com.citypulse.place.entity.Place;
import com.citypulse.place.entity.PlaceStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@EnabledIf("isDatabaseAvailable")
class PlaceRepositoryTest {

    @Autowired
    private PlaceRepository placeRepository;

    static boolean isDatabaseAvailable() {
        try (var socket = new java.net.Socket()) {
            socket.connect(new java.net.InetSocketAddress("127.0.0.1", 5432), 1000);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    @DisplayName("Should query seeded or inserted place by slug and status")
    void shouldFindPlaceBySlugAndStatus() {
        Place place = new Place("test-place-slug", "Test Place Name", "BEACH", "Quận Sơn Trà");
        place.setStatus(PlaceStatus.PUBLISHED);
        place.setRatingAverage(BigDecimal.valueOf(4.5));
        placeRepository.saveAndFlush(place);

        Optional<Place> found = placeRepository.findBySlugAndStatus("test-place-slug", PlaceStatus.PUBLISHED);
        assertTrue(found.isPresent());
        assertEquals("Test Place Name", found.get().getName());

        Optional<Place> notFound = placeRepository.findBySlugAndStatus("test-place-slug", PlaceStatus.DRAFT);
        assertFalse(notFound.isPresent());
    }
}
