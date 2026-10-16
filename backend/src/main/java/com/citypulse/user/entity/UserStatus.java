package com.citypulse.user.entity;

/**
 * Account/User lifecycle status.
 * Required: ACTIVE and LOCKED (BR-06: Locked accounts cannot be issued new sessions).
 */
public enum UserStatus {
    ACTIVE,
    LOCKED
}
