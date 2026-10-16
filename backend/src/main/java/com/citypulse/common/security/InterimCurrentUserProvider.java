package com.citypulse.common.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Interim implementation of CurrentUserProvider for standalone TV2 development and testing
 * before TV1's authentication and SecurityContext integration is completed.
 *
 * NOTE: This will be replaced by TV1's SecurityContext/JWT-based provider in production integration.
 */
@Component
public class InterimCurrentUserProvider implements CurrentUserProvider {

    /**
     * Development user ID used when security context is not yet populated by TV1 Auth.
     */
    public static final UUID DEV_FALLBACK_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private UUID activeUserId = DEV_FALLBACK_USER_ID;

    @Override
    public Optional<UUID> getCurrentUserId() {
        return Optional.ofNullable(activeUserId);
    }

    /**
     * Allows test suites to switch user context during tests.
     */
    public void setActiveUserId(UUID userId) {
        this.activeUserId = userId;
    }

    public void resetToDefault() {
        this.activeUserId = DEV_FALLBACK_USER_ID;
    }
}

