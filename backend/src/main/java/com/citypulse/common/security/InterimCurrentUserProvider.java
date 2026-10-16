package com.citypulse.common.security;

import com.citypulse.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of CurrentUserProvider that integrates with Spring Security's UserPrincipal.
 * In production, only authenticated requests with a valid UserPrincipal are permitted.
 * For isolated testing without SecurityContext, a test user ID can be explicitly injected.
 */
@Component
public class InterimCurrentUserProvider implements CurrentUserProvider {

    private UUID mockUserId = null;

    @Override
    public Optional<UUID> getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserPrincipal principal) {
            return Optional.ofNullable(principal.getId());
        }
        return Optional.ofNullable(mockUserId);
    }

    /**
     * Allows test suites to configure a mock user context during isolated tests.
     */
    public void setMockUserIdForTesting(UUID userId) {
        this.mockUserId = userId;
    }

    public void clearMockUserId() {
        this.mockUserId = null;
    }
}

