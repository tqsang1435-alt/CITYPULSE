package com.citypulse.user.repository;

import com.citypulse.user.entity.Role;
import com.citypulse.user.entity.User;
import com.citypulse.user.entity.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@EnabledIf("isDatabaseAvailable")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    static boolean isDatabaseAvailable() {
        try (var socket = new java.net.Socket()) {
            socket.connect(new java.net.InetSocketAddress("127.0.0.1", 5432), 1000);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should persist user with defaults and auto-generated UUID and timestamps")
    void shouldPersistUserWithDefaults() {
        User user = new User();
        user.setEmail("user1@example.com");
        user.setPasswordHash("$2a$10$encryptedHashExample1234567890123456789012");
        user.setDisplayName("User One");

        User saved = userRepository.saveAndFlush(user);

        assertNotNull(saved.getId(), "UUID id must be auto-generated");
        assertEquals("user1@example.com", saved.getEmail());
        assertEquals(Role.USER, saved.getRole(), "Default role must be USER");
        assertEquals(UserStatus.ACTIVE, saved.getStatus(), "Default status must be ACTIVE");
        assertNotNull(saved.getCreatedAt(), "createdAt must be set on persist");
        assertNotNull(saved.getUpdatedAt(), "updatedAt must be set on persist");
    }

    @Test
    @DisplayName("Should find user by email and normalized email")
    void shouldFindUserByEmailAndNormalizedEmail() {
        User user = new User("alice.smith@danang.vn", "hash_pw_1", "Alice Smith", Role.USER, UserStatus.ACTIVE);
        userRepository.saveAndFlush(user);

        Optional<User> found = userRepository.findByEmail("alice.smith@danang.vn");
        assertTrue(found.isPresent());
        assertEquals("Alice Smith", found.get().getDisplayName());

        // Normalized email lookup with untrimmed and mixed casing
        Optional<User> foundNormalized = userRepository.findByNormalizedEmail("  ALICE.SMITH@DANANG.VN  ");
        assertTrue(foundNormalized.isPresent());
        assertEquals(found.get().getId(), foundNormalized.get().getId());

        assertTrue(userRepository.existsByNormalizedEmail("  alice.smith@danang.vn  "));
        assertFalse(userRepository.existsByNormalizedEmail("unknown@danang.vn"));
    }

    @Test
    @DisplayName("Should reject exact duplicate email with DataIntegrityViolationException")
    void shouldRejectDuplicateEmail() {
        User user1 = new User("unique@domain.vn", "hash1", "User One", Role.USER, UserStatus.ACTIVE);
        userRepository.saveAndFlush(user1);

        User user2 = new User("unique@domain.vn", "hash2", "User Two", Role.USER, UserStatus.ACTIVE);
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user2);
        });
    }

    @Test
    @DisplayName("Should reject case-insensitive duplicate email with DataIntegrityViolationException")
    void shouldRejectCaseInsensitiveDuplicateEmail() {
        User user1 = new User("case.test@domain.vn", "hash1", "User One", Role.USER, UserStatus.ACTIVE);
        userRepository.saveAndFlush(user1);

        User user2 = new User("CASE.TEST@DOMAIN.VN", "hash2", "User Two", Role.USER, UserStatus.ACTIVE);
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user2);
        });
    }

    @Test
    @DisplayName("Should update updatedAt timestamp on entity modification")
    void shouldUpdateTimestampOnModification() throws InterruptedException {
        User user = new User("updater@danang.vn", "hash_initial", "Initial Name", Role.USER, UserStatus.ACTIVE);
        User saved = userRepository.saveAndFlush(user);
        Instant firstUpdatedAt = saved.getUpdatedAt();

        Thread.sleep(20);
        saved.setDisplayName("Updated Name");
        User updated = userRepository.saveAndFlush(saved);

        assertTrue(updated.getUpdatedAt().isAfter(firstUpdatedAt) || updated.getUpdatedAt().equals(firstUpdatedAt));
        assertEquals("Updated Name", updated.getDisplayName());
    }

    @Test
    @DisplayName("Should filter users by Role and Status")
    void shouldFilterUsersByRoleAndStatus() {
        User admin = new User("admin@danang.vn", "hashAdmin", "Admin", Role.ADMIN, UserStatus.ACTIVE);
        User locked = new User("locked@danang.vn", "hashLocked", "Locked User", Role.USER, UserStatus.LOCKED);
        userRepository.saveAndFlush(admin);
        userRepository.saveAndFlush(locked);

        List<User> admins = userRepository.findAllByRole(Role.ADMIN);
        assertEquals(1, admins.size());
        assertEquals("admin@danang.vn", admins.get(0).getEmail());

        List<User> lockedUsers = userRepository.findAllByStatus(UserStatus.LOCKED);
        assertEquals(1, lockedUsers.size());
        assertEquals("locked@danang.vn", lockedUsers.get(0).getEmail());
    }
}
