package com.citypulse.favorite.repository;

import com.citypulse.favorite.entity.Favorite;
import com.citypulse.place.entity.Place;
import com.citypulse.place.entity.PlaceStatus;
import com.citypulse.place.repository.PlaceRepository;
import com.citypulse.user.entity.Role;
import com.citypulse.user.entity.User;
import com.citypulse.user.entity.UserStatus;
import com.citypulse.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@EnabledIf("isDatabaseAvailable")
class FavoriteRepositoryTest {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

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
    @DisplayName("Should enforce unique constraint on (user_id, place_id)")
    void shouldEnforceUniqueConstraint() {
        User user = new User("fav.test@danang.vn", "hash", "Fav Test", Role.USER, UserStatus.ACTIVE);
        user = userRepository.saveAndFlush(user);

        Place place = new Place("fav-place-unique", "Fav Place", "BEACH", "Quận Sơn Trà");
        place = placeRepository.saveAndFlush(place);

        Favorite fav1 = new Favorite(user, place);
        favoriteRepository.saveAndFlush(fav1);

        Favorite fav2 = new Favorite(user, place);
        assertThrows(DataIntegrityViolationException.class, () -> {
            favoriteRepository.saveAndFlush(fav2);
        });
    }
}
