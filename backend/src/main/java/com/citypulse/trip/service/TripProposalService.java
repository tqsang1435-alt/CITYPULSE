package com.citypulse.trip.service;

import com.citypulse.common.exception.BusinessRuleException;
import com.citypulse.common.exception.ResourceNotFoundException;
import com.citypulse.trip.entity.Trip;
import com.citypulse.trip.entity.TripProposal;
import com.citypulse.trip.repository.TripProposalRepository;
import com.citypulse.trip.repository.TripRepository;
import com.citypulse.user.entity.AppUser;
import com.citypulse.user.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Quản lý đề xuất địa điểm trong chuyến đi (UC-05, UC-06).
 */
@Service
public class TripProposalService {

    private static final Logger log = LoggerFactory.getLogger(TripProposalService.class);

    private final TripRepository tripRepository;
    private final AppUserRepository userRepository;
    private final TripProposalRepository proposalRepository;
    private final TripPermissionService permissionService;

    public TripProposalService(
            TripRepository tripRepository,
            AppUserRepository userRepository,
            TripProposalRepository proposalRepository,
            TripPermissionService permissionService) {
        this.tripRepository     = tripRepository;
        this.userRepository     = userRepository;
        this.proposalRepository = proposalRepository;
        this.permissionService  = permissionService;
    }

    /**
     * UC-05: Đề xuất địa điểm.
     * Quyền: Owner hoặc Contributor (Viewer bị chặn 403).
     */
    @Transactional
    public ProposalResponse createProposal(Long tripId, Long callerUserId, CreateProposalRequest request) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyến đi: " + tripId));

        AppUser caller = userRepository.findById(callerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng: " + callerUserId));

        // Kiểm tra quyền: Owner hoặc Contributor
        permissionService.requireOwnerOrContributor(trip, callerUserId);

        TripProposal proposal = new TripProposal(
                trip,
                caller,
                request.placeId(),
                request.placeName().trim(),
                request.description() != null ? request.description().trim() : null
        );

        proposal = proposalRepository.save(proposal);

        log.info("User {} đã tạo proposal #{} ('{}') cho trip {}",
                callerUserId, proposal.getId(), proposal.getPlaceName(), tripId);

        return ProposalResponse.from(proposal);
    }

    /**
     * Lấy danh sách đề xuất trong chuyến đi.
     * Quyền: Mọi thành viên trong trip.
     */
    @Transactional(readOnly = true)
    public List<ProposalResponse> listProposals(Long tripId, Long callerUserId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyến đi: " + tripId));

        permissionService.requireMember(trip, callerUserId);

        return proposalRepository.findByTripIdOrderByCreatedAtDesc(tripId).stream()
                .map(ProposalResponse::from)
                .toList();
    }

    /**
     * UC-06: Duyệt hoặc từ chối đề xuất địa điểm.
     * Quyền: Chỉ Owner của chuyến đi.
     */
    @Transactional
    public ProposalResponse reviewProposal(Long tripId, Long proposalId, Long callerUserId, ReviewProposalRequest request) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyến đi: " + tripId));

        AppUser caller = userRepository.findById(callerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng: " + callerUserId));

        // Chỉ Owner mới được duyệt / từ chối
        permissionService.requireOwner(trip, callerUserId);

        TripProposal proposal = proposalRepository.findByIdAndTripId(proposalId, tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đề xuất #" + proposalId + " trong chuyến đi này."));

        if (!proposal.isPending()) {
            throw new BusinessRuleException("Đề xuất này đã được xử lý trước đó (trạng thái hiện tại: " + proposal.getStatus() + ").");
        }

        String note = request.reviewNote() != null ? request.reviewNote().trim() : null;
        if ("approved".equalsIgnoreCase(request.action())) {
            proposal.approve(caller, note);
            log.info("Owner {} đã duyệt proposal #{} trong trip {}", callerUserId, proposalId, tripId);
        } else {
            proposal.reject(caller, note);
            log.info("Owner {} đã từ chối proposal #{} trong trip {}", callerUserId, proposalId, tripId);
        }

        proposal = proposalRepository.save(proposal);

        return ProposalResponse.from(proposal);
    }
}

