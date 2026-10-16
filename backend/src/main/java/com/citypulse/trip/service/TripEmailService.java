package com.citypulse.trip.service;

/**
 * Interface gửi email — tách biệt khỏi InvitationService để dễ test và mock.
 */
public interface TripEmailService {

    /**
     * Gửi email mời tham gia chuyến đi.
     *
     * @param inviteeEmail email người được mời
     * @param tripTitle    tiêu đề chuyến đi
     * @param inviterName  tên người mời
     * @throws TripEmailException nếu gửi email thất bại
     */
    void sendTripInvitation(String inviteeEmail, String tripTitle, String inviterName);
}
