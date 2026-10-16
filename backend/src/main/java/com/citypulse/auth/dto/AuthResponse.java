package com.citypulse.auth.dto;

public record AuthResponse(
    String accessToken,
    String tokenType,
    long expiresInMs,
    UserResponse user
) {
    public AuthResponse(String accessToken, long expiresInMs, UserResponse user) {
        this(accessToken, "Bearer", expiresInMs, user);
    }
}
