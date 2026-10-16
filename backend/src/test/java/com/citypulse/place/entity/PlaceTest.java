package com.citypulse.place.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PlaceTest {

    @Test
    @DisplayName("Should create place and apply lifecycle defaults")
    void shouldCreatePlaceAndApplyDefaults() {
        Place place = new Place("bai-bien-my-khe", "Bãi biển Mỹ Khê", "BEACH", "Quận Sơn Trà");
        place.onCreate();

        assertThat(place.getSlug()).isEqualTo("bai-bien-my-khe");
        assertThat(place.getName()).isEqualTo("Bãi biển Mỹ Khê");
        assertThat(place.getCategory()).isEqualTo("BEACH");
        assertThat(place.getDistrict()).isEqualTo("Quận Sơn Trà");
        assertThat(place.getStatus()).isEqualTo(PlaceStatus.PUBLISHED);
        assertThat(place.getRatingAverage()).isEqualTo(BigDecimal.ZERO);
        assertThat(place.getReviewCount()).isEqualTo(0);
        assertThat(place.getDataQuality()).isEqualTo("VERIFIED");
        assertThat(place.getCreatedAt()).isNotNull();
        assertThat(place.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should update updatedAt on onUpdate callback")
    void shouldUpdateTimestampOnUpdate() {
        Place place = new Place("cau-rong", "Cầu Rồng", "CULTURE", "Quận Hải Châu");
        place.onCreate();
        var initialUpdatedAt = place.getUpdatedAt();

        place.onUpdate();

        assertThat(place.getUpdatedAt()).isNotNull();
        assertThat(place.getUpdatedAt()).isAfterOrEqualTo(initialUpdatedAt);
    }

    @Test
    @DisplayName("Should verify equals and hashCode based on ID")
    void shouldVerifyEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        Place p1 = new Place();
        p1.setId(id);
        p1.setName("Place 1");

        Place p2 = new Place();
        p2.setId(id);
        p2.setName("Place 2");

        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
    }
}
