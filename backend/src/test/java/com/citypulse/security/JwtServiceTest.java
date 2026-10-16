package com.citypulse.security;

import com.citypulse.user.entity.Role;
import com.citypulse.user.entity.User;
import com.citypulse.user.entity.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String TEST_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long EXPIRATION_MS = 3600000; // 1 hour

    private JwtService jwtService;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(TEST_SECRET, EXPIRATION_MS);
        testUser = new User("test@danangvista.vn", "hashed_password", "Test User", Role.USER, UserStatus.ACTIVE);
        testUser.setId(UUID.randomUUID());
    }

    @Test
    @DisplayName("JWT: Generates valid token containing correct subject and claims")
    void shouldGenerateValidTokenWithClaims() {
        String token = jwtService.generateToken(testUser);

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token));
        assertEquals(testUser.getId(), jwtService.extractUserId(token));
        assertEquals("test@danangvista.vn", jwtService.extractEmail(token));
        assertEquals("USER", jwtService.extractRole(token));
    }

    @Test
    @DisplayName("JWT: Validates token signature correctly")
    void shouldAcceptValidToken() {
        String token = jwtService.generateToken(testUser);

        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    @DisplayName("JWT: Rejects tampered or invalid token")
    void shouldRejectInvalidToken() {
        String token = jwtService.generateToken(testUser);
        String tamperedToken = token + "xyz123";

        assertFalse(jwtService.isTokenValid(tamperedToken));
        assertFalse(jwtService.isTokenValid("completely.invalid.token"));
        assertFalse(jwtService.isTokenValid(""));
        assertFalse(jwtService.isTokenValid(null));
    }

    @Test
    @DisplayName("JWT: Rejects expired token")
    void shouldRejectExpiredToken() {
        // Create JwtService with negative expiration (already expired)
        JwtService expiredJwtService = new JwtService(TEST_SECRET, -5000);
        String expiredToken = expiredJwtService.generateToken(testUser);

        assertFalse(jwtService.isTokenValid(expiredToken));
    }
}
