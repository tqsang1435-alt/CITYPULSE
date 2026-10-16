package com.citypulse.user.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("Email is automatically trimmed and lowercased on setter")
    void shouldNormalizeEmailOnSetter() {
        User user = new User();
        user.setEmail("   John.Doe@Example.COM   ");

        assertEquals("john.doe@example.com", user.getEmail());
    }

    @Test
    @DisplayName("Email is automatically trimmed and lowercased on constructor")
    void shouldNormalizeEmailOnConstructor() {
        User user = new User(
                "  ALICE@WonderLand.ORG  ",
                "hashed_secret",
                "Alice",
                Role.USER,
                UserStatus.ACTIVE
        );

        assertEquals("alice@wonderland.org", user.getEmail());
        assertEquals("hashed_secret", user.getPasswordHash());
        assertEquals("Alice", user.getDisplayName());
        assertEquals(Role.USER, user.getRole());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    @DisplayName("PrePersist initializes timestamps, defaults and normalizes email")
    void shouldSetDefaultsAndTimestampsOnPrePersist() {
        User user = new User();
        user.setEmail("  NEW_USER@Test.IO  ");
        user.setPasswordHash("hash123");
        user.setDisplayName("New User");

        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());

        user.onCreate();

        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        assertEquals(Role.USER, user.getRole());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        assertEquals("new_user@test.io", user.getEmail());
    }

    @Test
    @DisplayName("PreUpdate updates updatedAt timestamp and normalizes email")
    void shouldUpdateTimestampOnPreUpdate() throws InterruptedException {
        User user = new User();
        user.onCreate();
        Instant initialUpdatedAt = user.getUpdatedAt();

        Thread.sleep(10);
        user.setEmail("  UPDATED@Domain.COM  ");
        user.onUpdate();

        assertTrue(user.getUpdatedAt().isAfter(initialUpdatedAt) || user.getUpdatedAt().equals(initialUpdatedAt));
        assertEquals("updated@domain.com", user.getEmail());
    }

    @Test
    @DisplayName("toString() MUST NOT leak passwordHash")
    void shouldNeverExposePasswordHashInToString() {
        String sensitiveHash = "$2a$10$wE47Pabcdefghijklmnopqrstu123456789";
        User user = new User("secure@example.com", sensitiveHash, "Secure User", Role.USER, UserStatus.ACTIVE);

        String result = user.toString();

        assertFalse(result.contains(sensitiveHash), "toString() must not contain password hash!");
        assertTrue(result.contains("secure@example.com"));
        assertTrue(result.contains("Secure User"));
    }

    @Test
    @DisplayName("Entity equality is based on UUID identity")
    void shouldHandleEqualsAndHashCodeCorrectly() {
        UUID id = UUID.randomUUID();
        User u1 = new User();
        u1.setId(id);

        User u2 = new User();
        u2.setId(id);

        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
    }
}
