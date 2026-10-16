package com.citypulse.favorite.entity;

import com.citypulse.place.entity.Place;
import com.citypulse.user.entity.Role;
import com.citypulse.user.entity.User;
import com.citypulse.user.entity.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FavoriteTest {

    @Test
    @DisplayName("Should create Favorite and apply lifecycle defaults")
    void shouldCreateFavoriteAndApplyDefaults() {
        User user = new User("test@danangvista.vn", "hash", "Test User", Role.USER, UserStatus.ACTIVE);
        user.setId(UUID.randomUUID());

        Place place = new Place("bai-bien-my-khe", "Bãi biển Mỹ Khê", "BEACH", "Quận Sơn Trà");
        place.setId(UUID.randomUUID());

        Favorite favorite = new Favorite(user, place);
        favorite.onCreate();

        assertThat(favorite.getUser()).isEqualTo(user);
        assertThat(favorite.getPlace()).isEqualTo(place);
        assertThat(favorite.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should verify equals and hashCode based on ID")
    void shouldVerifyEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        Favorite f1 = new Favorite();
        f1.setId(id);

        Favorite f2 = new Favorite();
        f2.setId(id);

        assertThat(f1).isEqualTo(f2);
        assertThat(f1.hashCode()).isEqualTo(f2.hashCode());
    }
}
