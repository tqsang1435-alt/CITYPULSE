package com.citypulse.trip.service;

import com.citypulse.common.exception.AccessDeniedException;
import com.citypulse.common.exception.BusinessRuleException;
import com.citypulse.common.exception.ResourceNotFoundException;
import com.citypulse.trip.entity.Trip;
import com.citypulse.trip.entity.TripInvitation.Status;
import com.citypulse.trip.entity.TripMembership;
import com.citypulse.trip.entity.TripRole;
import com.citypulse.trip.repository.TripInvitationRepository;
import com.citypulse.trip.repository.TripMembershipRepository;
import com.citypulse.trip.repository.TripRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Quản lý thành viên và phân quyền trong chuyến đi (UC-07, UC-08).
 */
@Service
public class TripMembershipService {

    private static final Logger log = LoggerFactory.getLogger(TripMembershipService.class);

    private final TripRepository tripRepository;
    private final TripMembershipRepository membershipRepository;
    private final TripInvitationRepository invitationRepository;
    private final TripPermissionService permissionService;

    public TripMembershipService(
            TripRepository tripRepository,
            TripMembershipRepository membershipRepository,
            TripInvitationRepository invitationRepository,
            TripPermissionService permissionService) {
        this.tripRepository       = tripRepository;
        this.membershipRepository = membershipRepository;
        this.invitationRepository = invitationRepository;
        this.permissionService    = permissionService;
    }

    /**
     * UC-07: Xem danh sách thành viên & lời mời đang pending.
     * Mọi thành viên (Owner, Contributor, Viewer) đều có quyền xem.
     */
    @Transactional(readOnly = true)
    public TripMembersListResponse getMembers(Long tripId, Long callerUserId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyến đi: " + tripId));

        permissionService.requireMember(trip, callerUserId);

        List<TripMembership> memberships = membershipRepository.findByTripIdOrderByJoinedAtAsc(tripId);
        List<MemberResponse> memberResponses = new ArrayList<>();

        boolean ownerInMemberships = false;
        for (TripMembership m : memberships) {
            if (m.getUser().getId().equals(trip.getOwner().getId())) {
                ownerInMemberships = true;
            }
            memberResponses.add(MemberResponse.from(m));
        }

        // Đảm bảo owner luôn xuất hiện đầu danh sách
        if (!ownerInMemberships) {
            memberResponses.add(0, new MemberResponse(
                    null,
                    trip.getOwner().getId(),
                    trip.getOwner().getEmail(),
                    trip.getOwner().getDisplayName(),
                    TripRole.Owner,
                    trip.getCreatedAt()
            ));
        }

        // Lấy danh sách pending invitations
        List<InvitationResponse> pendingInvitations = invitationRepository
                .findByTripIdAndStatus(tripId, Status.pending)
                .stream()
                .map(InvitationResponse::from)
                .toList();

        return new TripMembersListResponse(trip.getId(), trip.getTitle(), memberResponses, pendingInvitations);
    }

    /**
     * UC-08: Cập nhật role thành viên (chỉ Owner).
     */
    @Transactional
    public MemberResponse updateMemberRole(Long tripId, Long targetUserId, TripRole newRole, Long callerUserId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyến đi: " + tripId));

        permissionService.requireOwner(trip, callerUserId);

        if (trip.getOwner().getId().equals(targetUserId)) {
            throw new BusinessRuleException("Không thể thay đổi vai trò của Owner chuyến đi.");
        }

        TripMembership membership = membershipRepository.findByTripIdAndUserId(tripId, targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Thành viên không tồn tại trong chuyến đi: " + targetUserId));

        membership.setRole(newRole);
        membership = membershipRepository.save(membership);

        log.info("Owner {} đã đổi role của user {} thành {} trong trip {}",
                callerUserId, targetUserId, newRole, tripId);

        return MemberResponse.from(membership);
    }

    /**
     * UC-08: Xóa thành viên khỏi chuyến đi (hoặc tự rời đi).
     * Luật: Không được xóa Owner của chuyến đi.
     */
    @Transactional
    public void removeMember(Long tripId, Long targetUserId, Long callerUserId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyến đi: " + tripId));

        // Kiểm tra quyền: phải là Owner hoặc tự xóa chính mình
        boolean isOwner = trip.getOwner().getId().equals(callerUserId);
        boolean isSelf = callerUserId.equals(targetUserId);

        if (!isOwner && !isSelf) {
            throw new AccessDeniedException("Chỉ Owner mới có quyền xóa thành viên khác.");
        }

        // Không được xóa Owner
        if (trip.getOwner().getId().equals(targetUserId)) {
            throw new BusinessRuleException("Không thể xóa Owner của chuyến đi.");
        }

        if (!membershipRepository.existsByTripIdAndUserId(tripId, targetUserId)) {
            throw new ResourceNotFoundException("Thành viên không tồn tại trong chuyến đi: " + targetUserId);
        }

        membershipRepository.deleteByTripIdAndUserId(tripId, targetUserId);

        log.info("Đã xóa user {} khỏi trip {} (bởi user {})", targetUserId, tripId, callerUserId);
    }
}

