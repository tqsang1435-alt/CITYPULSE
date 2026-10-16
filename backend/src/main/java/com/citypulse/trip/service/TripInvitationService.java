package com.citypulse.trip.service;

import com.citypulse.common.exception.AccessDeniedException;
import com.citypulse.common.exception.BusinessRuleException;
import com.citypulse.common.exception.ResourceNotFoundException;
import com.citypulse.trip.entity.Trip;
import com.citypulse.trip.entity.TripInvitation;
import com.citypulse.trip.entity.TripInvitation.Status;
import com.citypulse.trip.entity.TripMembership;
import com.citypulse.trip.entity.TripRole;
import com.citypulse.trip.repository.TripInvitationRepository;
import com.citypulse.trip.repository.TripMembershipRepository;
import com.citypulse.trip.repository.TripRepository;
import com.citypulse.user.entity.AppUser;
import com.citypulse.user.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * Business logic cho luồng Lời mời tham gia chuyến đi (UC-01 -> UC-04).
 */
@Service
public class TripInvitationService {

    private static final Logger log = LoggerFactory.getLogger(TripInvitationService.class);
    public static final int INVITATION_EXPIRY_DAYS = 7;

    private final TripRepository tripRepository;
    private final AppUserRepository userRepository;
    private final TripInvitationRepository invitationRepository;
    private final TripMembershipRepository membershipRepository;
    private final TripEmailService emailService;

    public TripInvitationService(
            TripRepository tripRepository,
            AppUserRepository userRepository,
            TripInvitationRepository invitationRepository,
            TripMembershipRepository membershipRepository,
            TripEmailService emailService) {
        this.tripRepository       = tripRepository;
        this.userRepository       = userRepository;
        this.invitationRepository = invitationRepository;
        this.membershipRepository = membershipRepository;
        this.emailService         = emailService;
    }

    /**
     * UC-01: Gửi lời mời tham gia chuyến đi.
     */
    @Transactional
    public InvitationResponse sendInvitation(Long tripId, Long callerUserId, String inviteeEmail) {
        // 1. Kiểm tra trip tồn tại
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy chuyến đi với ID: " + tripId));

        // 2. Kiểm tra caller tồn tại
        AppUser caller = userRepository.findById(callerUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng với ID: " + callerUserId));

        // 3. Kiểm tra caller là owner của trip
        if (!trip.getOwner().getId().equals(callerUserId)) {
            log.warn("User {} cố gắng gửi lời mời cho trip {} nhưng không phải owner",
                    callerUserId, tripId);
            throw new AccessDeniedException(
                    "Chỉ owner của chuyến đi mới có thể gửi lời mời.");
        }

        String normalizedEmail = inviteeEmail.trim().toLowerCase(Locale.ROOT);

        // 4. Kiểm tra đã có pending invitation chưa
        boolean alreadyPending = invitationRepository
                .existsByTripIdAndInviteeEmailIgnoreCaseAndStatus(tripId, normalizedEmail, Status.pending);
        if (alreadyPending) {
            throw new BusinessRuleException(
                    "Lời mời đã được gửi trước đó đến " + normalizedEmail);
        }

        // 5. Kiểm tra người được mời đã là thành viên (hoặc owner) chưa
        userRepository.findByEmailIgnoreCase(normalizedEmail).ifPresent(existingUser -> {
            boolean alreadyMember = trip.getOwner().getId().equals(existingUser.getId())
                    || (membershipRepository != null && membershipRepository.existsByTripIdAndUserId(tripId, existingUser.getId()));
            if (alreadyMember) {
                throw new BusinessRuleException("Người dùng đã là thành viên của chuyến đi.");
            }
        });

        // 6. Tạo Invitation mới
        Instant expiresAt = Instant.now().plus(INVITATION_EXPIRY_DAYS, ChronoUnit.DAYS);
        TripInvitation invitation = new TripInvitation(trip, caller, normalizedEmail, expiresAt);
        invitation = invitationRepository.save(invitation);

        // 7. Gửi email
        emailService.sendTripInvitation(normalizedEmail, trip.getTitle(), caller.getDisplayName());

        log.info("Đã tạo invitation #{} cho trip '{}' gửi đến {}",
                invitation.getId(), trip.getTitle(), normalizedEmail);

        return InvitationResponse.from(invitation);
    }

    /**
     * UC-02: Chấp nhận lời mời qua Token.
     */
    @Transactional
    public InvitationResponse acceptInvitationByToken(String token, Long userId) {
        TripInvitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lời mời với mã token đã cung cấp."));
        return doAcceptInvitation(invitation, userId);
    }

    /**
     * UC-02: Chấp nhận lời mời qua Trip ID & Invitation ID.
     */
    @Transactional
    public InvitationResponse acceptInvitation(Long tripId, Long invitationId, Long userId) {
        TripInvitation invitation = invitationRepository.findByIdAndTripId(invitationId, tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lời mời với ID: " + invitationId));
        return doAcceptInvitation(invitation, userId);
    }

    private InvitationResponse doAcceptInvitation(TripInvitation invitation, Long userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản người dùng: " + userId));

        // Kiểm tra trạng thái
        if (!invitation.isPending()) {
            throw new BusinessRuleException("Lời mời không ở trạng thái chờ phản hồi.");
        }

        // Kiểm tra hết hạn
        if (invitation.isExpired()) {
            invitation.expire();
            invitationRepository.save(invitation);
            throw new BusinessRuleException("Lời mời đã hết hạn.");
        }

        Trip trip = invitation.getTrip();

        // Kiểm tra đã là thành viên chưa
        boolean alreadyMember = trip.getOwner().getId().equals(userId)
                || (membershipRepository != null && membershipRepository.existsByTripIdAndUserId(trip.getId(), userId));
        if (alreadyMember) {
            throw new BusinessRuleException("Bạn đã tham gia rồi.");
        }

        // Chuyển trạng thái invitation -> accepted
        invitation.accept();
        invitationRepository.save(invitation);

        // Tạo Membership với role = Viewer
        TripMembership membership = new TripMembership(trip, user, TripRole.Viewer);
        if (membershipRepository != null) {
            membershipRepository.save(membership);
        }

        log.info("User {} đã chấp nhận invitation #{} và trở thành Viewer của trip {}",
                userId, invitation.getId(), trip.getId());

        return InvitationResponse.from(invitation);
    }

    /**
     * UC-03: Từ chối lời mời qua Token.
     */
    @Transactional
    public InvitationResponse declineInvitationByToken(String token, Long userId) {
        TripInvitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lời mời với mã token đã cung cấp."));
        return doDeclineInvitation(invitation, userId);
    }

    /**
     * UC-03: Từ chối lời mời qua Trip ID & Invitation ID.
     */
    @Transactional
    public InvitationResponse declineInvitation(Long tripId, Long invitationId, Long userId) {
        TripInvitation invitation = invitationRepository.findByIdAndTripId(invitationId, tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lời mời với ID: " + invitationId));
        return doDeclineInvitation(invitation, userId);
    }

    private InvitationResponse doDeclineInvitation(TripInvitation invitation, Long userId) {
        if (!invitation.isPending()) {
            throw new BusinessRuleException("Lời mời không ở trạng thái chờ phản hồi.");
        }

        if (invitation.isExpired()) {
            invitation.expire();
            invitationRepository.save(invitation);
            throw new BusinessRuleException("Lời mời đã hết hạn.");
        }

        invitation.decline();
        invitationRepository.save(invitation);

        log.info("User {} đã từ chối invitation #{}", userId, invitation.getId());

        return InvitationResponse.from(invitation);
    }

    /**
     * UC-04: Hủy lời mời đã gửi (chỉ Owner).
     */
    @Transactional
    public InvitationResponse cancelInvitation(Long tripId, Long invitationId, Long callerUserId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyến đi với ID: " + tripId));

        if (!trip.getOwner().getId().equals(callerUserId)) {
            throw new AccessDeniedException("Chỉ owner của chuyến đi mới có thể hủy lời mời.");
        }

        TripInvitation invitation = invitationRepository.findByIdAndTripId(invitationId, tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lời mời với ID: " + invitationId));

        if (!invitation.isPending()) {
            throw new BusinessRuleException("Chỉ có thể hủy lời mời đang chờ phản hồi (pending).");
        }

        invitation.cancel();
        invitationRepository.save(invitation);

        log.info("Owner {} đã hủy invitation #{} của trip {}", callerUserId, invitationId, tripId);

        return InvitationResponse.from(invitation);
    }
}
