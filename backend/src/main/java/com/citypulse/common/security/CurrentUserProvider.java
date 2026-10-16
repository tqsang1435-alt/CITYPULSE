package com.citypulse.common.security;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import java.util.Optional;
import java.util.UUID;

/**
 * Contract to resolve the current authenticated user's ID.
 * This decouples TV2 (Trip & Stop) from TV1's authentication and token parsing implementation.
 */
public interface CurrentUserProvider {

    /**
     * Resolves the ID of the currently authenticated user.
     *
     * @return Optional containing current user's UUID, or empty if unauthenticated.
     */
    Optional<UUID> getCurrentUserId();

    /**
     * Resolves the current user's UUID or throws an exception if unauthenticated.
     */
    default UUID requireCurrentUserId() {
        return getCurrentUserId()
            .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Authentication required: current user context not available."));
    }
}

