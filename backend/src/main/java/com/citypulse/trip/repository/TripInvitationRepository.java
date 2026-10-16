package com.citypulse.trip.repository;

import com.citypulse.trip.entity.TripInvitation;
import com.citypulse.trip.entity.TripInvitation.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripInvitationRepository extends JpaRepository<TripInvitation, Long> {

    /**
     * Kiểm tra đã có lời mời pending cho (tripId, inviteeEmail) chưa.
     * Đây là bước kiểm tra trước khi insert để trả về lỗi nghiệp vụ thân thiện.
     * DB partial unique index là lớp bảo vệ cuối cùng chống race condition.
     */
    boolean existsByTripIdAndInviteeEmailIgnoreCaseAndStatus(Long tripId, String inviteeEmail, Status status);

    /** Lấy tất cả lời mời của một trip */
    List<TripInvitation> findByTripId(Long tripId);

    /** Lấy tất cả lời mời theo trip và status (ví dụ pending) */
    List<TripInvitation> findByTripIdAndStatus(Long tripId, Status status);

    /** Tìm lời mời bằng bảo mật token gửi trong email */
    Optional<TripInvitation> findByToken(String token);

    /** Tìm lời mời theo ID và tripId */
    Optional<TripInvitation> findByIdAndTripId(Long id, Long tripId);
}
