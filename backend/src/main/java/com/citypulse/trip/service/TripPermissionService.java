package com.citypulse.trip.service;

import com.citypulse.common.exception.AccessDeniedException;
import com.citypulse.trip.entity.Trip;
import com.citypulse.trip.entity.TripMembership;
import com.citypulse.trip.entity.TripRole;
import com.citypulse.trip.repository.TripMembershipRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service kiểm tra phân quyền người dùng trong chuyến đi theo ma trận quyền:
 * - Owner: toàn quyền (mời/hủy lời mời, duyệt đề xuất, đổi role, xóa thành viên).
 * - Contributor: đề xuất địa điểm, xem chuyến đi & thành viên.
 * - Viewer: chỉ xem chuyến đi & thành viên.
 */
@Service
public class TripPermissionService {

    private final TripMembershipRepository membershipRepository;

    public TripPermissionService(TripMembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    /**
     * Xác định vai trò của user trong trip.
     * Owner của trip luôn có role là Owner.
     */
    public Optional<TripRole> getMemberRole(Trip trip, Long userId) {
        if (trip.getOwner().getId().equals(userId)) {
            return Optional.of(TripRole.Owner);
        }
        return membershipRepository.findByTripIdAndUserId(trip.getId(), userId)
                .map(TripMembership::getRole);
    }

    public boolean isMember(Trip trip, Long userId) {
        return getMemberRole(trip, userId).isPresent();
    }

    /**
     * Yêu cầu quyền Owner. Ném AccessDeniedException nếu không phải Owner.
     */
    public void requireOwner(Trip trip, Long userId) {
        Optional<TripRole> role = getMemberRole(trip, userId);
        if (role.isEmpty() || role.get() != TripRole.Owner) {
            throw new AccessDeniedException("Chỉ owner của chuyến đi mới có quyền thực hiện thao tác này.");
        }
    }

    /**
     * Yêu cầu quyền Owner hoặc Contributor (dùng cho tạo proposal).
     */
    public void requireOwnerOrContributor(Trip trip, Long userId) {
        Optional<TripRole> role = getMemberRole(trip, userId);
        if (role.isEmpty()) {
            throw new AccessDeniedException("Bạn không phải thành viên của chuyến đi.");
        }
        if (!role.get().canPropose()) {
            throw new AccessDeniedException("Viewer không có quyền thực hiện thao tác này.");
        }
    }

    /**
     * Yêu cầu người dùng là thành viên bất kỳ (Owner, Contributor, Viewer).
     */
    public void requireMember(Trip trip, Long userId) {
        if (!isMember(trip, userId)) {
            throw new AccessDeniedException("Bạn không phải thành viên của chuyến đi.");
        }
    }
}

