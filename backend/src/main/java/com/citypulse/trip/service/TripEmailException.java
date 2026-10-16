package com.citypulse.trip.service;

/**
 * Ném ra khi không thể gửi email mời.
 * Đây là RuntimeException có checked: InvitationService sẽ bắt nó
 * và rollback transaction nếu cần, đảm bảo không lưu Invitation khi email thất bại.
 */
public class TripEmailException extends RuntimeException {

    public TripEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
