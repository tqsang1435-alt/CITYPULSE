package com.citypulse.report.entity;

/**
 * Lifecycle states of an incoming user report.
 * Aligns with SRS Section 3.9 and TV5 Week 1 Specification.
 */
public enum ReportStatus {
    OPEN,
    IN_REVIEW,
    RESOLVED,
    REJECTED
}
