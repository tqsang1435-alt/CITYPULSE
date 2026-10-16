package com.citypulse.user.service;

public record AuthResponse(String token, Long id, String email, String displayName) {}
