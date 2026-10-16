package com.citypulse.common.security;

import com.citypulse.security.UserPrincipal;
import com.citypulse.user.entity.Role;
import com.citypulse.user.entity.UserStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InterimCurrentUserProviderTest {

    private InterimCurrentUserProvider provider;

    @BeforeEach
    void setUp() {
        provider = new InterimCurrentUserProvider();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("getCurrentUserId returns principal ID when UserPrincipal is in SecurityContext")
    void getCurrentUserId_ShouldReturnUserId_WhenAuthenticatedWithUserPrincipal() {
        UUID expectedId = UUID.randomUUID();
        UserPrincipal principal = new UserPrincipal(
            expectedId,
            "test@citypulse.vn",
            "hashed_pwd",
            "Test User",
            Role.USER,
            UserStatus.ACTIVE
        );

        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Optional<UUID> actualId = provider.getCurrentUserId();

        assertTrue(actualId.isPresent());
        assertEquals(expectedId, actualId.get());
        assertEquals(expectedId, provider.requireCurrentUserId());
    }

    @Test
    @DisplayName("getCurrentUserId returns empty when unauthenticated and no mock ID set")
    void getCurrentUserId_ShouldReturnEmpty_WhenUnauthenticated() {
        Optional<UUID> actualId = provider.getCurrentUserId();

        assertFalse(actualId.isPresent(), "Unauthenticated request must NOT have a default fallback user");
    }

    @Test
    @DisplayName("requireCurrentUserId throws AuthenticationCredentialsNotFoundException when unauthenticated")
    void requireCurrentUserId_ShouldThrow_WhenUnauthenticated() {
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> {
            provider.requireCurrentUserId();
        });
    }

    @Test
    @DisplayName("getCurrentUserId returns mock ID when explicitly configured for testing")
    void getCurrentUserId_ShouldReturnMockId_WhenExplicitlySet() {
        UUID mockId = UUID.randomUUID();
        provider.setMockUserIdForTesting(mockId);

        Optional<UUID> actualId = provider.getCurrentUserId();

        assertTrue(actualId.isPresent());
        assertEquals(mockId, actualId.get());

        provider.clearMockUserId();
        assertFalse(provider.getCurrentUserId().isPresent());
    }
}

