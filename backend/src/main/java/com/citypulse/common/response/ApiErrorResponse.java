package com.citypulse.common.response;

import java.time.Instant;
import java.util.List;

/**
 * Standard error response structure for CITYPULSE REST APIs.
 */
public record ApiErrorResponse(
    int status,
    String error,
    String message,
    Instant timestamp,
    List<String> details
) {
    public ApiErrorResponse(int status, String error, String message) {
        this(status, error, message, Instant.now(), List.of());
    }

    public ApiErrorResponse(int status, String error, String message, List<String> details) {
        this(status, error, message, Instant.now(), details != null ? details : List.of());
    }
}

